package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record AnsweredQuestionPostsByMemberResponse(
	Long questionPostId,
	String title,
	String content,
	String jobGroup,
	int reward,
	String questionPostUpdatedAt,
	boolean isChosen,
	int savedTotalCount,
	int recommendTotalCount,
	Long answerId,
	String answerContent,
	String answerUpdatedAt
) {

	@QueryProjection
	public AnsweredQuestionPostsByMemberResponse(
		QuestionPost questionPost,
		int savedTotalCount,
		int recommendTotalCount,
		Answer answer) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getUpdatedAt().toString(),
			questionPost.getIsChosen(),
			savedTotalCount,
			recommendTotalCount,
			answer.getId(),
			answer.getContent(),
			answer.getUpdatedAt().toString()
		);
	}
}
