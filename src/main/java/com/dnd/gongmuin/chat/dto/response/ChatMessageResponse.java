package com.dnd.gongmuin.chat.dto.response;

public record ChatMessageResponse(
	Long senderId,
	String content,
	String type,
	boolean isRead,
	String createdAt
) {
}
