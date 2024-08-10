package com.dnd.gongmuin.answer.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterAnswerRequest(

	@NotBlank(message = "답변을 입력해주세요.")
	String content
) {
}