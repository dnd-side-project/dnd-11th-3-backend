package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record AnsweredQuestionPostsResponse(
	Long questionPostId,
	String questionTitle,
	String questionContent,
	String jobGroup,
	int reward,
	String questionPostCreatedAt,
	boolean isChosen,
	int bookmarkCount,
	int recommendCount,
	Long answerId,
	String answerContent,
	String answerCreatedAt
) {

	@QueryProjection
	public AnsweredQuestionPostsResponse(
		QuestionPost questionPost,
		int bookmarkCount,
		int recommendCount,
		Answer answer) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getCreatedAt().toString(),
			questionPost.getIsChosen(),
			bookmarkCount,
			recommendCount,
			answer.getId(),
			answer.getContent(),
			answer.getCreatedAt().toString()
		);
	}
}
