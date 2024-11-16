package com.dnd.gongmuin.chat_inquiry.dto;

public record AcceptChatResponse(
	Long createdChatRoomId,
	String inquiryStatus,
	int credit
) {
}
