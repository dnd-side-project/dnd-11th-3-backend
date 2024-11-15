package com.dnd.gongmuin.chatroom.dto.response;

public record ChatMessageResponse(
	Long senderId,
	String content,
	String type,
	boolean isRead,
	String createdAt
) {
}
