package com.dnd.gongmuin.answer.dto;

import com.dnd.gongmuin.question_post.dto.MemberInfo;

public record AnswerDetailResponse(
	Long answerId,
	String content,
	boolean isChosen,
	boolean isQuestioner,
	MemberInfo memberInfo,
	String createdAt
) {

}
