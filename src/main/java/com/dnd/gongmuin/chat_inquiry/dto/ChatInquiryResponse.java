package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.question_post.dto.response.MemberInfo;
import com.querydsl.core.annotations.QueryProjection;

public record ChatInquiryResponse(
	Long chatInquiryId,
	String message,
	String inquiryStatus,
	boolean isInquirer,
	MemberInfo partnerInfo
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
			new MemberInfo(
				partnerId,
				partnerNickname,
				partnerJobGroup.getLabel(),
				partnerProfileImageNo
			)
		);
	}
}
