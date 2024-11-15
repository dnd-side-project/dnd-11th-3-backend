package com.dnd.gongmuin.chat_inquiry.dto;

public record CreateChatInquiryResponse(
	Long chatInquiryId,
	String message,
	String chatStatus,
	Long partnerId,
	String partnerNickname,
	String partnerJobGroup,
	int partnerProfileImageNo
) {
}
