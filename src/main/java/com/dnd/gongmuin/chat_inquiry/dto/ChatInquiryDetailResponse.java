package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.member.dto.response.MemberInfo;

public record ChatInquiryDetailResponse(
	Long chatInquiryId,
	String inquiryMessage,
	String inquiryStatus,
	boolean isInquirer,
	MemberInfo chatPartner
) {
}
