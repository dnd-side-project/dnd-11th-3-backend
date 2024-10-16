package com.dnd.gongmuin.chat.dto;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.domain.MessageType;
import com.dnd.gongmuin.chat.dto.request.ChatMessageRequest;
import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageMapper {

	private static final String REQUEST_MESSAGE_POSTFIX = "님이 채팅을 요청하셨습니다.";

	public static ChatMessageResponse toChatMessageResponse(
		ChatMessage chatMessage
	) {
		return new ChatMessageResponse(
			chatMessage.getMemberId(),
			chatMessage.getContent(),
			chatMessage.getType().getLabel(),
			chatMessage.getIsRead(),
			chatMessage.getCreatedAt().toString()
		);
	}

	public static ChatMessage toChatMessage(
		ChatMessageRequest request,
		long chatRoomId
	) {
		return ChatMessage.of(
			request.content(),
			chatRoomId,
			request.senderId(),
			MessageType.of(request.type())
		);
	}

	public static ChatMessage toFirstChatMessage(
		ChatRoom chatRoom
	) {
		return ChatMessage.of(
			chatRoom.getInquirer().getNickname() + REQUEST_MESSAGE_POSTFIX,
			chatRoom.getId(),
			chatRoom.getInquirer().getId(),
			MessageType.of(MessageType.TEXT.getLabel())
		);
	}
}
