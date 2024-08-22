package com.dnd.gongmuin.question_post.dto.response;

import java.util.List;

public record QuestionPostDetailResponse(
	Long questionPostId,
	String title,
	String content,
	List<String> imageUrls,
	int reward,
	String targetJobGroup,
	MemberInfo memberInfo,
	boolean isSaved,
	boolean isRecommended,
	int savedCount,
	int recommendCount,
	String createdAt
) {
}