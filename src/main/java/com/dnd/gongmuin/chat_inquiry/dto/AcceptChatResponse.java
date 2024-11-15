package com.dnd.gongmuin.chat_inquiry.dto;

public record AcceptChatResponse(
	Long createdChatRoomId,
	String chatStatus,
	int credit
) {
}
