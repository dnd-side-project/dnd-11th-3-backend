package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.post_interaction.domain.PostInteractionCount;
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
	Long interactionCountId,
	int totalCount,
	String interactionType,
	Long answerId,
	String answerContent,
	String answerUpdatedAt
) {

	@QueryProjection
	public AnsweredQuestionPostsByMemberResponse(
		QuestionPost questionPost, PostInteractionCount interactionCount, Answer answer) {
		this(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getUpdatedAt().toString(),
			questionPost.getIsChosen(),
			extractId(interactionCount),
			extractTotalCount(interactionCount),
			extractTypeLabel(interactionCount),
			answer.getId(),
			answer.getContent(),
			answer.getUpdatedAt().toString()
		);
	}

	private static Long extractId(PostInteractionCount interactionCount) {
		return interactionCount != null ? interactionCount.getId() : null;
	}

	private static int extractTotalCount(PostInteractionCount interactionCount) {
		return interactionCount != null ? interactionCount.getTotalCount() : 0;
	}

	private static String extractTypeLabel(PostInteractionCount interactionCount) {
		return interactionCount != null ? interactionCount.getType().getLabel() : null;
	}
}