package com.dnd.gongmuin.auth.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	UNSUPPORTED_SOCIAL_LOGIN("해당 소셜 로그인은 지원되지 않습니다.", "AUHT_001");

	private final String message;
	private final String code;
}
