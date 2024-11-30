package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.member.dto.response.MemberInfo;

public record CreateChatInquiryResponse(
	Long chatInquiryId,
	String inquiryMessage,
	String inquiryStatus,
	int credit,
	MemberInfo chatPartner
) {
}
