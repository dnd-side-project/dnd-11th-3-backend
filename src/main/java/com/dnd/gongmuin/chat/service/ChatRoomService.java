package com.dnd.gongmuin.chat.service;

import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.dto.ChatMessageMapper;
import com.dnd.gongmuin.chat.dto.ChatRoomMapper;
import com.dnd.gongmuin.chat.dto.request.CreateChatRoomRequest;
import com.dnd.gongmuin.chat.dto.response.AcceptChatResponse;
import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chat.dto.response.RejectChatResponse;
import com.dnd.gongmuin.chat.exception.ChatErrorCode;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.chat.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final MemberRepository memberRepository;
	private final QuestionPostRepository questionPostRepository;

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
		return ChatRoomMapper.toChatRoomDetailResponse(
			chatRoomRepository.save(
				ChatRoomMapper.toChatRoom(questionPost, inquirer, answerer)
			),
			answerer
		);
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

		return ChatRoomMapper.toAcceptChatResponse(chatRoom);
	}

	@Transactional
	public RejectChatResponse rejectChat(Long chatRoomId, Member member) {
		ChatRoom chatRoom = getChatRoomById(chatRoomId);
		validateIfAnswerer(member, chatRoom);
		chatRoom.updateStatusRejected();

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

