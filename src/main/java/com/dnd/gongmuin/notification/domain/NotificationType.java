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
	CHAT_REQUEST("채팅신청"),
	CHAT_REJECT("채팅거절"),
	CHAT_ACCEPT("채팅수락");

	private final String label;

	public static NotificationType from(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(() -> new NotFoundException(NotificationErrorCode.NOT_FOUND_NOTIFICATION_TYPE));
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
