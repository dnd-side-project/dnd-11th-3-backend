package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.member.dto.response.MemberInfo;

public record ChatInquiryDetailResponse(
	Long chatInquiryId,
	String inquiryMessage,
	String inquiryStatus,
	boolean isInquirer,
	int memberCredit,
	Long questionPostId,
	String targetJobGroup,
	String title,
	MemberInfo chatPartner,
	String createdAt
) {
}
