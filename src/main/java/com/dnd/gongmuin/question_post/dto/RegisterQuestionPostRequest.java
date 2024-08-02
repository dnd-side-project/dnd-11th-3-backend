package com.dnd.gongmuin.question_post.dto;

import java.util.List;

public record RegisterQuestionPostRequest(
	String title,
	String content,
	List<String> imageUrls,
	int reward,
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
