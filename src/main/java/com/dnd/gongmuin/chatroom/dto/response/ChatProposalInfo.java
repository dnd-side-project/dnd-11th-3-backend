package com.dnd.gongmuin.chatroom.dto.response;

import com.dnd.gongmuin.chatroom.domain.InquiryStatus;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.querydsl.core.annotations.QueryProjection;

public record ChatProposalInfo(
	Long chatRoomId,
	String chatStatus,
	boolean isInquirer,
	Long partnerId,
	String partnerNickname,
	String partnerJobGroup,
	int partnerProfileImageNo
) {
	@QueryProjection
	public ChatProposalInfo(
		Long chatRoomId,
		InquiryStatus inquiryStatus,
		boolean isInquirer,
		Long partnerId,
		String partnerNickname,
		JobGroup partnerJobGroup,
		int partnerProfileImageNo
	) {
		this(
			chatRoomId,
			inquiryStatus.getLabel(),
			isInquirer,
			partnerId,
			partnerNickname,
			partnerJobGroup.getLabel(),
			partnerProfileImageNo
		);
	}
}
