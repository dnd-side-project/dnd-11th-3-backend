package com.dnd.gongmuin.notification.dto.response;

public record readNotificationResponse(
	Long notificationId,
	Boolean isRead
) {
}
