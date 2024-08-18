package com.dnd.gongmuin.question_post.dto.response;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record RecQuestionPostResponse(
	Long questionPostId,
	String title,
	int reward,
	boolean isChosen,
	int savedCount,
	int recommendCount
) {

	@QueryProjection
	public RecQuestionPostResponse(
		QuestionPost questionPost,
		int savedCount,
		int recommendCount
	) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getReward(),
			questionPost.getIsChosen(),
			savedCount,
			recommendCount
		);
	}
}
