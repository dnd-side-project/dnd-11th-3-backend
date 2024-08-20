package com.dnd.gongmuin.security.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
	UNAUTHORIZED_USER("비인가 사용자 요청입니다.", "SECURITY_001"),
	FORBIDDEN_USER("ROLE_USER 권한이 필요합니다.", "SECURITY_002"),
	FORBIDDEN_GUEST("ROLE_GUEST 권한이 필요합니다.", "SECURITY_003"),
	FORBIDDEN_MISMATCH("어떤 권한도 매치되지 않습니다.", "SECURITY_004");
	private final String message;
	private final String code;
}
