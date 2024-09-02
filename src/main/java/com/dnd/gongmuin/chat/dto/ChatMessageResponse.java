package com.dnd.gongmuin.chat.dto;

public record ChatMessageResponse(
	Long senderId,
	Long chatRoomId,
	String content,
	String type
) {
}
