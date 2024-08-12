package com.dnd.gongmuin.security.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {

	MALFORMED_TOKEN("알맞지 않은 형식의 토큰입니다.", "JWT_001"),
	INVALID_TOKEN("유효하지 않은 토큰입니다.", "JWT_002");

	private final String message;
	private final String code;

}