package com.dnd.gongmuin.member.dto.response;

public record MemberProfileResponse(
	Long memberId,
	String nickname,
	String jobGroup,
	String jobCategory,
	int credit,
	int profileImageNo
) {
}
