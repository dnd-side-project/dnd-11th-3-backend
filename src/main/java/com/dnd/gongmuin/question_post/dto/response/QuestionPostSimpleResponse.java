package com.dnd.gongmuin.question_post.dto.response;

import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
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
		InteractionCount savedCount,
		InteractionCount recommendCount
	) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getCreatedAt().toString(),
			questionPost.getIsChosen(),
			getCount(savedCount),
			getCount(recommendCount)
		);
	}

	private static int getCount(InteractionCount interactionCount) {
		return interactionCount != null ? interactionCount.getCount() : 0;
	}
}
