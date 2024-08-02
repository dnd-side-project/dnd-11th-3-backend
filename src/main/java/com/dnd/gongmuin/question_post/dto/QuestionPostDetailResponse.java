package com.dnd.gongmuin.question_post.dto;

import java.util.List;

public record QuestionPostDetailResponse(
	Long questionPostId,
	String title,
	String content,
	List<String> imageUrls,
	int reward,
	String targetJobGroup,
	MemberInfo memberInfo
) {
	public static QuestionPostDetailResponse of(
		Long questionPostId,
		String title,
		String content,
		List<String> imageUrls,
		int reward,
		String targetJobGroup,
		Long memberId,
		String nickname,
		String memberJobGroup
	) {
		return new QuestionPostDetailResponse(
			questionPostId,
			title,
			content,
			imageUrls,
			reward,
			targetJobGroup,
			MemberInfo.of(memberId, nickname, memberJobGroup)
		);
	}
}
