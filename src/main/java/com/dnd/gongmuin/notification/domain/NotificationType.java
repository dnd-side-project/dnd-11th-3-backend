package com.dnd.gongmuin.notification.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

	ANSWER("댓글"),
	CHOSEN("채택"),
	CHAT("채팅");

	private final String label;

	public static NotificationType of(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
