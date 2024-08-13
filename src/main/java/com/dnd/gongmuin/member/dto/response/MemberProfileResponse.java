package com.dnd.gongmuin.member.dto.response;

public record MemberProfileResponse(
	String nickname,
	String jobGroup,
	String jobCategory,
	int credit
) {
}
