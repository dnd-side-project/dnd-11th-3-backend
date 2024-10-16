package com.dnd.gongmuin.security.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2ErrorCode implements ErrorCode {

	INVALID_REQUEST("유효하지 않은 탈퇴 요청입니다.", "OAUTH2_001"),
	EXPIRED_AUTH_TOKEN("만료된 OAuth2 토큰입니다.", "OAUTH2_002"),
	INTERNAL_SERVER_ERROR("OAuth 서버 에러 발생입니다.", "OAUTH2_003");
	private final String message;
	private final String code;
}
