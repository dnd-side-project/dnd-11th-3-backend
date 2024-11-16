package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.question_post.dto.response.MemberInfo;
import com.querydsl.core.annotations.QueryProjection;

public record ChatInquiryResponse(
	Long chatInquiryId,
	String inquiryMessage,
	String inquiryStatus,
	boolean isInquirer,
	MemberInfo chatPartner
) {
	@QueryProjection
	public ChatInquiryResponse(
		Long chatInquiryId,
		String inquiryMessage,
		InquiryStatus inquiryStatus,
		boolean isInquirer,
		Long partnerId,
		String partnerNickname,
		JobGroup partnerJobGroup,
		int partnerProfileImageNo
	) {
		this(
			chatInquiryId,
			inquiryMessage,
			inquiryStatus.getLabel(),
			isInquirer,
			new MemberInfo(
				partnerId,
				partnerNickname,
				partnerJobGroup.getLabel(),
				partnerProfileImageNo
			)
		);
	}
}
