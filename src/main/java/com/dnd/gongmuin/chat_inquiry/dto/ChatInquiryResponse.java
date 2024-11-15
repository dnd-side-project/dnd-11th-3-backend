package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.querydsl.core.annotations.QueryProjection;

public record ChatInquiryResponse(
	Long chatInquiryId,
	String message,
	String chatStatus,
	boolean isInquirer,
	Long partnerId,
	String partnerNickname,
	String partnerJobGroup,
	int partnerProfileImageNo
) {
	@QueryProjection
	public ChatInquiryResponse(
		Long chatInquiryId,
		String message,
		InquiryStatus inquiryStatus,
		boolean isInquirer,
		Long partnerId,
		String partnerNickname,
		JobGroup partnerJobGroup,
		int partnerProfileImageNo
	) {
		this(
			chatInquiryId,
			message,
			inquiryStatus.getLabel(),
			isInquirer,
			partnerId,
			partnerNickname,
			partnerJobGroup.getLabel(),
			partnerProfileImageNo
		);
	}
}
