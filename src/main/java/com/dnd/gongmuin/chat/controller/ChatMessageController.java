package com.dnd.gongmuin.chat.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.dnd.gongmuin.chat.dto.ChatMessageRequest;
import com.dnd.gongmuin.chat.dto.ChatMessageResponse;
import com.dnd.gongmuin.chat.service.ChatMessageService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

	private final ChatMessageService chatMessageService;

	@MessageMapping("/chat-rooms/{chatRoomId}")
	@SendTo("/sub/chat-rooms/{chatRoomId}")
	public ChatMessageResponse sendMessage(
		@DestinationVariable("chatRoomId") Long chatRoomId,
		@Payload ChatMessageRequest request
	) {
		return chatMessageService.saveChatMessage(request, chatRoomId);
	}
}
