package com.dnd.gongmuin.member.dto.response;

public record MemberInfo(
	Long memberId,
	String nickname,
	String memberJobGroup,
	int profileImageNo
) {
}
