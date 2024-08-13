package com.dnd.gongmuin.question_post.dto.request;

import java.util.List;

import jakarta.validation.constraints.Size;

public record QuestionPostSearchCondition(
	String keyword,
	@Size(max = 3, message = "직군은 3개까지 선택 가능합니다.")
	List<String> jobGroups,
	Boolean isChosen
) {
}
