package com.dnd.gongmuin.notification.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

	NOT_FOUND_NOTIFICATION_TYPE("알맞은 알림 타입을 찾을 수 없습니다.", "NOTIFICATION_001"),
	SAVE_NOTIFICATION_FAILED("알림 저장을 실패했습니다.", "NOTIFICATION_002"),
	NOTIFICATIONS_BY_MEMBER_FAILED("알림 목록을 불러오는데 실패했습니다.", "NOTIFICATION_003"),
	NOT_FOUND_NOTIFICATION("해당 알림을 찾을 수 없습니다", "NOTIFICATION_004"),
	CHANGE_IS_READ_NOTIFICATION_FAILED("해당 알림의 읽음 여부 변경을 실패했습니다.", "NOTIFICATION_005"),
	INVALID_NOTIFICATION_OWNER("해당 알림의 주인이 아닙니다.", "NOTIFICATION_006");

	private final String message;
	private final String code;
}
