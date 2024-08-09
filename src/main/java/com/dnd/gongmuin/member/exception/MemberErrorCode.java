package com.dnd.gongmuin.member.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	NOT_FOUND_MEMBER("특정 회원을 찾을 수 없습니다.", "MEMBER_001"),
	NOT_FOUND_NEW_MEMBER("신규 회원이 아닙니다.", "MEMBER_002"),
	NOT_ENOUGH_CREDIT("보유한 크레딧이 부족합니다.", "MEMBER_003");

	private final String message;
	private final String code;
}
