package com.dnd.gongmuin.chat.dto.response;

import com.dnd.gongmuin.chat.domain.ChatStatus;
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
		ChatStatus chatStatus,
		boolean isInquirer,
		Long partnerId,
		String partnerNickname,
		JobGroup partnerJobGroup,
		int partnerProfileImageNo
	) {
		this(
			chatRoomId,
			chatStatus.getLabel(),
			isInquirer,
			partnerId,
			partnerNickname,
			partnerJobGroup.getLabel(),
			partnerProfileImageNo
		);
	}
}
