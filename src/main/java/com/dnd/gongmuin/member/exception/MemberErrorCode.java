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
	UPDATE_PROFILE_FAILED("프로필 수정에 실패했습니다.", "MEMBER_005"),
	QUESTION_POSTS_BY_MEMBER_FAILED("마이페이지 게시글 목록을 불러오는데 실패했습니다", "MEMBER_006"),
	NOT_FOUND_JOB_GROUP("직군을 올바르게 입력해주세요.", "MEMBER_007"),
	NOT_FOUND_JOB_CATEGORY("직렬을 올바르게 입력해주세요.", "MEMBER_008");

	private final String message;
	private final String code;
}
