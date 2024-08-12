package com.dnd.gongmuin.question_post.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record QuestionPostSearchCondition(
	@NotBlank(message = "검색어를 입력해주세요.")
	String keyword,
	List<String> jobGroups,
	Boolean isChosen
) {
}
