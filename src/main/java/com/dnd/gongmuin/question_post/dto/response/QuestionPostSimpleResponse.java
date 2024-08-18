package com.dnd.gongmuin.question_post.dto.response;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record QuestionPostSimpleResponse(
	Long questionPostId,
	String title,
	String content,
	String jobGroup,
	int reward,
	String createdAt,
	boolean isChosen,
	int savedCount,
	int recommendCount
) {

	@QueryProjection
	public QuestionPostSimpleResponse(
		QuestionPost questionPost,
		int savedCount,
		int recommendCount
	) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getCreatedAt().toString(),
			questionPost.getIsChosen(),
			savedCount,
			recommendCount
		);
	}
}
