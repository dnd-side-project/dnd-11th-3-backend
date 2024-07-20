package com.dnd.gongmuin.notification.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

	ANSWER("댓글"),
	CHOSEN("채택"),
	CHAT("채팅");

	private final String msg;
}
