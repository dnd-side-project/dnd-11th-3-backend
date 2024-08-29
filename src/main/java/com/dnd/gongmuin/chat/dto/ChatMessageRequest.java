package com.dnd.gongmuin.chat.dto;

public record ChatMessageRequest(
	String content,
	String type,
	String mediaUrl
) {
}
