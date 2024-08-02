package com.dnd.gongmuin.question_post.dto;

public record MemberInfo(
	Long memberId,
	String nickname,
	String memberJobGroup
) {
	public static MemberInfo of(
		Long memberId,
		String nickname,
		String memberJobGroup
	) {
		return new MemberInfo(
			memberId,
			nickname,
			memberJobGroup
		);
	}
}
