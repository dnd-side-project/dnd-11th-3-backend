package com.dnd.gongmuin.notification.dto;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.NotificationType;

public record NotificationEvent(
	NotificationType type,
	Long targetId,
	Long triggerMemberId,
	Member toMember
) {
}
