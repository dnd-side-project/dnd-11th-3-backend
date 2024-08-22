package com.dnd.gongmuin.question_post.dto.response;

public record MemberInfo(
	Long memberId,
	String nickname,
	String memberJobGroup,
	int profileImageNo
) {
}
