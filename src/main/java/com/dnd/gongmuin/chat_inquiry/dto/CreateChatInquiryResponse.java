package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.question_post.dto.response.MemberInfo;

public record CreateChatInquiryResponse(
	Long chatInquiryId,
	String inquiryMessage,
	String inquiryStatus,
	MemberInfo chatPartner
) {
}
