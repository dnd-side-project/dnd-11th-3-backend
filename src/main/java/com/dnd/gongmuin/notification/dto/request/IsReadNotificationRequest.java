package com.dnd.gongmuin.notification.dto.request;

import jakarta.validation.constraints.NotNull;

public record IsReadNotificationRequest(

	@NotNull(message = "알림 ID 값은 필수 값 입니다.")
	Long notificationId
) {
}
