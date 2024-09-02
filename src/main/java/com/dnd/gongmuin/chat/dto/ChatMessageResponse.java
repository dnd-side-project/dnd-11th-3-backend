package com.dnd.gongmuin.chat.dto;

public record ChatMessageResponse(
	Long senderId,
	String content,
	String type,
	String createdAt
) {
}
