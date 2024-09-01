package com.dnd.gongmuin.notification.dto;

import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.dto.response.IsReadNotificationResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationMapper {

	public static IsReadNotificationResponse toIsReadNotificationResponse(Notification notification) {
		return new IsReadNotificationResponse(
			notification.getId(),
			notification.getIsRead()
		);
	}
}
