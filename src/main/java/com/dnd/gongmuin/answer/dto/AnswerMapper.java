package com.dnd.gongmuin.answer.dto;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.dto.response.MemberInfo;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerMapper {

	public static Answer toAnswer(
		Long questionPostId,
		boolean isQuestioner,
		RegisterAnswerRequest request,
		Member member
	) {
		return Answer.of(
			request.content(),
			isQuestioner,
			questionPostId,
			member
		);
	}

	public static AnswerDetailResponse toAnswerDetailResponse(Answer answer) {
		Member member = answer.getMember();
		return new AnswerDetailResponse(
			answer.getId(),
			answer.getContent(),
			answer.getIsChosen(),
			answer.getIsQuestioner(),
			new MemberInfo(
				member.getId(),
				member.getNickname(),
				member.getJobGroup().getLabel(),
				member.getProfileImageNo()
			),
			answer.getCreatedAt().toString()
		);
	}
}
