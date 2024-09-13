package com.dnd.gongmuin.notification.dto;

import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.dto.response.readNotificationResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMapper {

	public static readNotificationResponse toIsReadNotificationResponse(Notification notification) {
		return new readNotificationResponse(
			notification.getId(),
			notification.getIsRead()
		);
	}
}
