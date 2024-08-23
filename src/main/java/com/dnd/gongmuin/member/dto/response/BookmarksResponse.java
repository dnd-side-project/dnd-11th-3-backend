package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record BookmarksResponse(
	Long questionPostId,
	String questionTitle,
	String questionContent,
	String jobGroup,
	int reward,
	String createdAt,
	boolean isChosen,
	int bookmarkCount,
	int recommendCount
) {
	@QueryProjection
	public BookmarksResponse(
		QuestionPost questionPost,
		int bookmarkCount,
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
			bookmarkCount,
			recommendCount
		);
	}
}
