package com.dnd.gongmuin.notification.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

	NOT_FOUND_NOTIFICATION_TYPE("알맞은 알림 타입을 찾을 수 없습니다.", "NOTIFICATION_001"),
	SAVE_NOTIFICATION_FAILED("알림 저장을 실패했습니다.", "NOTIFICATION_002"),
	NOTIFICATIONS_BY_MEMBER_FAILED("알림 목록을 불러오는데 실패했습니다.", "NOTIFICATION_003");

	private final String message;
	private final String code;
}
