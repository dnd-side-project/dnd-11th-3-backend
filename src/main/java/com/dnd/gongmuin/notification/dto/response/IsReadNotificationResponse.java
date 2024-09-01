package com.dnd.gongmuin.notification.dto.response;

public record IsReadNotificationResponse(
	Long notificationId,
	Boolean isRead
) {
}
