package com.dnd.gongmuin.security.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
	UNAUTHORIZED_USER("비인가 사용자 요청입니다.", "SECURITY_001");

	private final String message;
	private final String code;
}
