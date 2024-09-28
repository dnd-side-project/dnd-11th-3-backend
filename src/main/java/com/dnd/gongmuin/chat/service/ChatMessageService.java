package com.dnd.gongmuin.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.dto.ChatMessageMapper;
import com.dnd.gongmuin.chat.dto.request.ChatMessageRequest;
import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;

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
			ChatMessageMapper.toChatMessage(request, chatRoomId, senderId));
		log.info("chatRoomId = {}, senderId= {}, chatMessageId= {}", chatRoomId, senderId, chatMessage.getId());
		return ChatMessageMapper.toChatMessageResponse(chatMessage);
	}
}
