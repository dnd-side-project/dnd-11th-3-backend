package com.dnd.gongmuin.chat.dto;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.domain.MessageType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMapper {

	public static ChatMessageResponse toChatMessageResponse(
		ChatMessage chatMessage
	) {
		return new ChatMessageResponse(
			chatMessage.getMemberId(),
			chatMessage.getChatRoomId(),
			chatMessage.getContent(),
			chatMessage.getType().getLabel(),
			chatMessage.getMediaUrl()
		);
	}

	public static ChatMessage toChatMessage(
		ChatMessageRequest request,
		long chatRoomId,
		long memberId
	) {
		return ChatMessage.of(
			request.content(),
			chatRoomId,
			memberId,
			request.mediaUrl(),
			MessageType.of(request.type())
		);
	}
}
