package com.dnd.gongmuin.question_post.dto.response;

public record QuestionPostSimpleResponse(
	Long questionPostId,
	String title,
	String content,
	String jobGroup,
	int reward,
	String createdAt,
	boolean isChosen
	// TODO: 8/11/24 북마크 수, 추천수 추가
) {
}
