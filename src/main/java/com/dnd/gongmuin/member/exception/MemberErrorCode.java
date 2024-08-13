package com.dnd.gongmuin.member.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

	NOT_FOUND_MEMBER("특정 회원을 찾을 수 없습니다.", "MEMBER_001"),
	NOT_FOUND_NEW_MEMBER("신규 회원이 아닙니다.", "MEMBER_002"),
	LOGOUT_FAILED("로그아웃을 실패했습니다.", "MEMBER_003"),
	NOT_ENOUGH_CREDIT("보유한 크레딧이 부족합니다.", "MEMBER_004"),
	UPDATE_PROFILE_FAILED("프로필 수정에 실패했습니다.", "MEMBER_005");

	private final String message;
	private final String code;
}
