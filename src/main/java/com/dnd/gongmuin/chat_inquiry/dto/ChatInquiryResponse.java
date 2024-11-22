package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.dto.response.MemberInfo;
import com.querydsl.core.annotations.QueryProjection;

public record ChatInquiryResponse(
	Long chatInquiryId,
	String inquiryMessage,
	String inquiryStatus,
	boolean isInquirer,
	MemberInfo chatPartner,
	String createdAt
) {
	@QueryProjection
	public ChatInquiryResponse(
		ChatInquiry chatInquiry,
		boolean isInquirer
	) {
		this(
			chatInquiry.getId(),
			chatInquiry.getMessage(),
			chatInquiry.getStatus().getLabel(),
			isInquirer,
			createPartnerInfo(isInquirer, chatInquiry),
			chatInquiry.getCreatedAt().toString()
		);
	}

	private static MemberInfo createPartnerInfo(boolean isInquirer, ChatInquiry chatInquiry) {
		Member partner = isInquirer ? chatInquiry.getAnswerer() : chatInquiry.getInquirer();
		return new MemberInfo(
			partner.getId(),
			partner.getNickname(),
			partner.getJobGroup().getLabel(),
			partner.getProfileImageNo()
		);
	}
}
