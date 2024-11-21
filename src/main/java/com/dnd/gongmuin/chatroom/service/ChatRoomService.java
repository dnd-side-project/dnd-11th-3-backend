package com.dnd.gongmuin.chatroom.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.chatroom.dto.ChatMessageMapper;
import com.dnd.gongmuin.chatroom.dto.ChatRoomMapper;
import com.dnd.gongmuin.chatroom.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chatroom.dto.response.LatestChatMessage;
import com.dnd.gongmuin.chatroom.exception.ChatErrorCode;
import com.dnd.gongmuin.chatroom.repository.ChatMessageQueryRepository;
import com.dnd.gongmuin.chatroom.repository.ChatMessageRepository;
import com.dnd.gongmuin.chatroom.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatMessageRepository chatMessageRepository;
	private final ChatMessageQueryRepository chatMessageQueryRepository;
	private final ChatRoomRepository chatRoomRepository;

	@Transactional(readOnly = true)
	public PageResponse<ChatMessageResponse> getChatMessages(Long chatRoomId, Pageable pageable) {
		Slice<ChatMessageResponse> responsePage = chatMessageRepository
			.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable)
			.map(ChatMessageMapper::toChatMessageResponse);
		return PageMapper.toPageResponse(responsePage);
	}

	@Transactional(readOnly = true)
	public PageResponse<ChatRoomSimpleResponse> getChatRoomsByMember(Member member, Pageable pageable) {
		// 회원 채팅방 정보 가져오기
		Slice<ChatRoomInfo> chatRoomInfos = chatRoomRepository.getChatRoomsByMember(
			member, pageable
		);

		// chatRoomId 리스트 추출
		List<Long> chatRoomIds = chatRoomInfos.stream()
			.map(ChatRoomInfo::chatRoomId)
			.toList();

		// 각 채팅방 최근 메시지 가져오기
		List<LatestChatMessage> latestChatMessages
			= chatMessageQueryRepository.findLatestChatByChatRoomIds(chatRoomIds);

		// 두 객체 합쳐서 하나의 DTO로 반환
		List<ChatRoomSimpleResponse> responses = getChatRoomSimpleResponses(latestChatMessages,
			chatRoomInfos);

		// PageResponse 객체 생성
		return new PageResponse<>(responses, responses.size(), chatRoomInfos.hasNext());
	}

	@Transactional(readOnly = true)
	public ChatRoomDetailResponse getChatRoomById(Long chatRoomId, Member member) {
		ChatRoom chatRoom = getChatRoomById(chatRoomId);
		Member chatPartner = getChatPartner(member, chatRoom);
		return ChatRoomMapper.toChatRoomDetailResponse(chatRoom, chatPartner);
	}

	private List<ChatRoomSimpleResponse> getChatRoomSimpleResponses(List<LatestChatMessage> latestChatMessages,
		Slice<ChatRoomInfo> chatRoomInfos) {
		// <chatRoomId, LatestMessage> -> 순서 보장 x
		Map<Long, LatestChatMessage> messageMap = latestChatMessages.stream()
			.collect(Collectors.toMap(LatestChatMessage::chatRoomId, message -> message));

		// 최신순 정렬 및 변환
		return chatRoomInfos.stream()
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
	}

	private ChatRoom getChatRoomById(Long id) {
		return chatRoomRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(ChatErrorCode.NOT_FOUND_CHAT_ROOM));
	}

	private Member getChatPartner(Member member, ChatRoom chatRoom) {
		if (member.equals(chatRoom.getAnswerer())) {
			return chatRoom.getInquirer();
		}
		if (member.equals(chatRoom.getInquirer())) {
			return chatRoom.getAnswerer();
		}
		throw new ValidationException(ChatErrorCode.UNAUTHORIZED_CHAT_ROOM);
	}
}
