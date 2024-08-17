package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record QuestionPostsByMemberResponse(
	Long questionPostId,
	String title,
	String content,
	String jobGroup,
	int reward,
	String updatedAt,
	boolean isChosen,
	int savedTotalCount,
	int recommendTotalCount
) {

	@QueryProjection
	public QuestionPostsByMemberResponse(
		QuestionPost questionPost,
		int savedTotalCount,
		int recommendTotalCount
	) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getUpdatedAt().toString(),
			questionPost.getIsChosen(),
			savedTotalCount,
			recommendTotalCount
		);
	}
}
