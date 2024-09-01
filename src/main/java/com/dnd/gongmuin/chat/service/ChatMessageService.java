package com.dnd.gongmuin.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.dto.ChatMapper;
import com.dnd.gongmuin.chat.dto.ChatMessageRequest;
import com.dnd.gongmuin.chat.dto.ChatMessageResponse;
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
		Long memberId = request.memberId();
		ChatMessage chatMessage = chatMessageRepository.save(ChatMapper.toChatMessage(request, chatRoomId, memberId));
		log.info("chatRoomId = {}, memberId= {}, chatMessageId= {}", chatRoomId, memberId, chatMessage.getId());
		return ChatMapper.toChatMessageResponse(chatMessage);
	}
}
