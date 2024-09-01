package com.dnd.gongmuin.notification.domain;

import java.util.Arrays;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.notification.exception.NotificationErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

	ANSWER("답변"),
	CHOSEN("채택"),
	CHAT("채팅");

	private final String label;

	public static NotificationType of(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(() -> new NotFoundException(NotificationErrorCode.NOT_FOUND_NOTIFICATION_TYPE));
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
