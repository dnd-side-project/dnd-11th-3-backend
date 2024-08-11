package com.dnd.gongmuin.question_post.dto.request;

import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterQuestionPostRequest(

	@Size(min = 2, max = 20, message = "제목은 2자 이상 20자 이하여야 합니다.")
	String title,
	@Size(min = 10, max = 200, message = "본문은 10자 이상 200자 이하여야 합니다.")
	String content,
	List<String> imageUrls,
	@Min(value = 2_000, message = "리워드는 2000 이상이어야 합니다.")
	@Max(value = 10_000, message = "리워드는 10000 이하여야 합니다.")
	int reward,
	@NotBlank(message = "직군을 입력해주세요.")
	String targetJobGroup
) {
	public static RegisterQuestionPostRequest of(
		String title,
		String content,
		List<String> imageUrls,
		int reward,
		String targetJobGroup
	) {
		return new RegisterQuestionPostRequest(
			title, content, imageUrls, reward, targetJobGroup
		);
	}
}
