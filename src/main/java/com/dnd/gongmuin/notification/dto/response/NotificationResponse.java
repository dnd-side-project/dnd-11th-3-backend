package com.dnd.gongmuin.notification.dto.response;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.Notification;
import com.querydsl.core.annotations.QueryProjection;

public record NotificationResponse(

	Long notificationId,

	String type,

	Boolean isRead,

	Long targetId,

	Long triggerMemberId,

	String triggerMemberNickName,

	Long targetMemberId,

	String NotificationCreatedAt
) {

	@QueryProjection
	public NotificationResponse(
		Notification notification,
		Member triggerMember
	) {
		this(
			notification.getId(),
			notification.getType().getLabel(),
			notification.getIsRead(),
			notification.getTargetId(),
			triggerMember.getId(),
			triggerMember.getNickname(),
			notification.getMember().getId(),
			notification.getCreatedAt().toString()
		);
	}
}
