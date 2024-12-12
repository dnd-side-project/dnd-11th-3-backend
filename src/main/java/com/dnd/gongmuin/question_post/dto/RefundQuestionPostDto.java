package com.dnd.gongmuin.question_post.dto;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.querydsl.core.annotations.QueryProjection;

public record RefundQuestionPostDto(
	Long questionPostId,
	int reward,
	Member member
) {
	@QueryProjection
	public RefundQuestionPostDto(
		QuestionPost questionPost
	) {
		this(questionPost.getId(),
			questionPost.getReward(),
			questionPost.getMember()
		);
	}
}
