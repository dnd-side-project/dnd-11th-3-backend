package com.dnd.gongmuin.question_post.dto.response;

import java.util.List;

public record ModifyQuestionPostResponse(
	Long questionPostId,
	String title,
	String content,
	List<String> imageUrls,
	int reward,
	String targetJobGroup
) {
}
