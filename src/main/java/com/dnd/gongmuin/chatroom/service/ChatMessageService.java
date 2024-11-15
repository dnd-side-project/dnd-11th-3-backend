package com.dnd.gongmuin.chatroom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chatroom.domain.ChatMessage;
import com.dnd.gongmuin.chatroom.dto.ChatMessageMapper;
import com.dnd.gongmuin.chatroom.dto.request.ChatMessageRequest;
import com.dnd.gongmuin.chatroom.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chatroom.repository.ChatMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

	private final ChatMessageRepository chatMessageRepository;

	@Transactional
	public ChatMessageResponse saveChatMessage(
		ChatMessageRequest request,
		Long chatRoomId
	) {
		Long senderId = request.senderId();
		ChatMessage chatMessage = chatMessageRepository.save(
			ChatMessageMapper.toChatMessage(request, chatRoomId));
		log.info("chatRoomId = {}, senderId= {}, chatMessageId= {}", chatRoomId, senderId, chatMessage.getId());
		return ChatMessageMapper.toChatMessageResponse(chatMessage);
	}
}
