package com.dnd.gongmuin.common.fixture;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.domain.NotificationType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationFixture {

	public static Notification notification(
		NotificationType type,
		Long questionPostId,
		Long triggerMemberId,
		Member member) {
		return Notification.of(
			type,
			questionPostId,
			triggerMemberId,
			member
		);
	}
}
