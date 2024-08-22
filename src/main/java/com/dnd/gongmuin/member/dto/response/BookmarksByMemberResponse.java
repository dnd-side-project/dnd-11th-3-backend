package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record BookmarksByMemberResponse(
	Long questionPostId,
	String title,
	String content,
	String jobGroup,
	int reward,
	String createdAt,
	boolean isChosen,
	int savedTotalCount,
	int recommendTotalCount
) {
	@QueryProjection
	public BookmarksByMemberResponse(
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
			questionPost.getCreatedAt().toString(),
			questionPost.getIsChosen(),
			savedTotalCount,
			recommendTotalCount
		);
	}
}
