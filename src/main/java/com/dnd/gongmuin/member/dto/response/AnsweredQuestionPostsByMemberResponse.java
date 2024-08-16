package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
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
		InteractionCount savedCount,
		InteractionCount recommendCount,
		Answer answer) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getUpdatedAt().toString(),
			questionPost.getIsChosen(),
			extractTotalCount(savedCount),
			extractTotalCount(recommendCount),
			answer.getId(),
			answer.getContent(),
			answer.getUpdatedAt().toString()
		);
	}

	private static int extractTotalCount(InteractionCount interactionCount) {
		return interactionCount != null ? interactionCount.getCount() : 0;
	}
}
