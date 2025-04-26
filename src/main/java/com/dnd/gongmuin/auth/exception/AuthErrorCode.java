package com.dnd.gongmuin.auth.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	UNSUPPORTED_SOCIAL_LOGIN("해당 소셜 로그인은 지원되지 않습니다.", "AUTH_001"),
	NOT_FOUND_PROVIDER("알맞은 Provider를 찾을 수 없습니다.", "AUTH_002"),
	NOT_FOUND_AUTH("회원의 AUTH를 찾을 수 없습니다.", "AUTH_003"),
	UNAUTHORIZED_TOKEN("잘못된 토큰입니다.", "AUTH_004"),
	FAIL_REISSUE_TOKEN("토큰 재발급에 실패했습니다.", "AUTH_005"),
	MISSING_ACCESS_TOKEN("AT가 존재하지 않습니다.", "AUTH_006");

	private final String message;
	private final String code;
}
