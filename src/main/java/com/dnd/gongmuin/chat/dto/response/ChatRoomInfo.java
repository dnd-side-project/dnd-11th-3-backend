package com.dnd.gongmuin.chat.dto.response;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.querydsl.core.annotations.QueryProjection;

public record ChatRoomInfo(
	Long chatRoomId,
	Long partnerId,
	String partnerNickname,
	String partnerJobGroup,
	int partnerProfileImageNo
) {
	@QueryProjection
	public ChatRoomInfo(
		Long chatRoomId,
		Long partnerId,
		String partnerNickname,
		JobGroup partnerJobGroup,
		int partnerProfileImageNo
	) {
		this(
			chatRoomId,
			partnerId,
			partnerNickname,
			partnerJobGroup.getLabel(),
			partnerProfileImageNo
		);
	}
}
