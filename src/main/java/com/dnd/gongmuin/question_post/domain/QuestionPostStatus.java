package com.dnd.gongmuin.question_post.domain;

import java.util.Arrays;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionPostStatus {

	ANSWER_WAITING("답변대기"),
	ANSWER_CLOSED("답변마감"),
	CHOSEN_WAITING("채택대기"),
	CHOSEN_COMPLETED("채택완료");

	private final String status;

	public static QuestionPostStatus from(String status) {
		return Arrays.stream(values())
			.filter(questionPostStatus -> questionPostStatus.getStatus().equalsIgnoreCase(status))
			.findFirst()
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST_STATUS));
	}
}
