package com.dnd.gongmuin.question_post.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategory {

	COMMON("전체"),
	TASK("업무"),
	JOB_CATEGORY("직렬");

	private final String label;
}
