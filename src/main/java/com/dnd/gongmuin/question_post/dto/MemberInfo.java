package com.dnd.gongmuin.question_post.dto;

public record MemberInfo(
	Long memberId,
	String nickname,
	String memberJobGroup
	// TODO: 추후 프로필 이미지 타입 추가
) {}
