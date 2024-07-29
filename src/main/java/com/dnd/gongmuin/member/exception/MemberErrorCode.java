package com.dnd.gongmuin.member.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	NOT_FOUND_MEMBER("특정 회원을 찾을 수 없습니다.", "MEMBER_001"),
	NOT_FOUND_NEWMEMBER("신규 회원이 아닙니다.", "MEMBER_002");

	private final String message;
	private final String code;
}
