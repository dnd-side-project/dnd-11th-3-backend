package com.dnd.gongmuin.chat.service;

import static com.dnd.gongmuin.notification.domain.NotificationType.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.domain.ChatStatus;
import com.dnd.gongmuin.chat.dto.ChatMessageMapper;
import com.dnd.gongmuin.chat.dto.ChatRoomMapper;
import com.dnd.gongmuin.chat.dto.request.CreateChatRoomRequest;
import com.dnd.gongmuin.chat.dto.response.AcceptChatResponse;
import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chat.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chat.dto.response.LatestChatMessage;
import com.dnd.gongmuin.chat.dto.response.RejectChatResponse;
import com.dnd.gongmuin.chat.exception.ChatErrorCode;
import com.dnd.gongmuin.chat.repository.ChatMessageQueryRepository;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.chat.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.notification.dto.NotificationEvent;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatMessageQueryRepository chatMessageQueryRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final QuestionPostRepository questionPostRepository;
	private final ApplicationEventPublisher eventPublisher;

	private static void validateIfAnswerer(Member member, ChatRoom chatRoom) {
		if (!Objects.equals(member.getId(), chatRoom.getAnswerer().getId())) {
			throw new ValidationException(ChatErrorCode.UNAUTHORIZED_REQUEST);
		}
	}

	@Transactional(readOnly = true)
	public PageResponse<ChatMessageResponse> getChatMessages(Long chatRoomId, Pageable pageable) {
		Slice<ChatMessageResponse> responsePage = chatMessageRepository
			.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable)
			.map(ChatMessageMapper::toChatMessageResponse);
		return PageMapper.toPageResponse(responsePage);
	}

	@Transactional
	public ChatRoomDetailResponse createChatRoom(CreateChatRoomRequest request, Member inquirer) {
		QuestionPost questionPost = getQuestionPostById(request.questionPostId());
		Member answerer = getMemberById(request.answererId());

		ChatRoom chatRoom = chatRoomRepository.save(
			ChatRoomMapper.toChatRoom(questionPost, inquirer, answerer)
		);

		eventPublisher.publishEvent(
			new NotificationEvent(CHAT_REQUEST, chatRoom.getId(), inquirer.getId(), answerer)
		);

		return ChatRoomMapper.toChatRoomDetailResponse(
			chatRoom,
			answerer
		);
	}

	@Transactional(readOnly = true)
	public PageResponse<ChatRoomSimpleResponse> getChatRoomsByMember(Member member, String chatStatus,
		Pageable pageable) {
		// 회원 채팅방 정보 가져오기
		Slice<ChatRoomInfo> chatRoomInfos = chatRoomRepository.getChatRoomsByMember(
			member, ChatStatus.from(chatStatus), pageable
		);

		// chatRoomId 리스트 추출
		List<Long> chatRoomIds = chatRoomInfos.stream()
			.map(ChatRoomInfo::chatRoomId)
			.toList();

		// 각 채팅방 최근 메시지 정보
		List<LatestChatMessage> latestChatMessages
			= chatMessageQueryRepository.findLatestChatByChatRoomIds(chatRoomIds);

		// <chatRoomId, LatestMessage> -> 순서 보장 x
		Map<Long, LatestChatMessage> messageMap = latestChatMessages.stream()
			.collect(Collectors.toMap(LatestChatMessage::chatRoomId, message -> message));

		// 최신순 정렬 및 변환
		List<ChatRoomSimpleResponse> responsePage = chatRoomInfos.stream()
			.sorted(
				Comparator.comparing(
					(ChatRoomInfo info) -> messageMap.get(info.chatRoomId()).createdAt()
				).reversed())
			.map(chatRoomInfo -> {
				LatestChatMessage latestMessage = messageMap.get(chatRoomInfo.chatRoomId());
				return ChatRoomMapper.toChatRoomSimpleResponse(
					chatRoomInfo, latestMessage
				);
			}).toList();

		// PageResponse 객체 생성
		return new PageResponse<>(responsePage, responsePage.size(), chatRoomInfos.hasNext());
	}

	@Transactional(readOnly = true)
	public ChatRoomDetailResponse getChatRoomById(Long chatRoomId, Member member) {
		ChatRoom chatRoom = getChatRoomById(chatRoomId);
		Member chatPartner = getChatPartner(member.getId(), chatRoom);
		return ChatRoomMapper.toChatRoomDetailResponse(chatRoom, chatPartner);
	}

	@Transactional
	public AcceptChatResponse acceptChat(Long chatRoomId, Member member) {
		ChatRoom chatRoom = getChatRoomById(chatRoomId);
		validateIfAnswerer(member, chatRoom);
		chatRoom.updateStatusAccepted();

		eventPublisher.publishEvent(
			new NotificationEvent(CHAT_ACCEPT, chatRoom.getId(), member.getId(), chatRoom.getInquirer())
		);

		return ChatRoomMapper.toAcceptChatResponse(chatRoom);
	}

	@Transactional
	public RejectChatResponse rejectChat(Long chatRoomId, Member member) {
		ChatRoom chatRoom = getChatRoomById(chatRoomId);
		validateIfAnswerer(member, chatRoom);
		chatRoom.updateStatusRejected();

		eventPublisher.publishEvent(
			new NotificationEvent(CHAT_REJECT, chatRoom.getId(), member.getId(), chatRoom.getInquirer())
		);

		return ChatRoomMapper.toRejectChatResponse(chatRoom);
	}

	private ChatRoom getChatRoomById(Long id) {
		return chatRoomRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ChatErrorCode.NOT_FOUND_CHAT_ROOM));
	}

	private QuestionPost getQuestionPostById(Long id) {
		return questionPostRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
	}

	private Member getMemberById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));
	}

	private Member getChatPartner(Long memberId, ChatRoom chatRoom) {
		if (Objects.equals(chatRoom.getAnswerer().getId(), memberId)) {
			return chatRoom.getInquirer();
		} else if (Objects.equals(chatRoom.getInquirer().getId(), memberId)) {
			return chatRoom.getAnswerer();
		} else {
			throw new ValidationException(ChatErrorCode.UNAUTHORIZED_CHAT_ROOM);
		}
	}
}

