package com.dnd.gongmuin.question_post.dto.response;

import java.util.List;

import com.dnd.gongmuin.member.dto.response.MemberInfo;

public record RegisterQuestionPostResponse(
	Long questionPostId,
	String title,
	String content,
	List<String> imageUrls,
	int reward,
	String targetJobGroup,
	String status,
	MemberInfo memberInfo,
	int remainingCredit,
	String createdAt
) {
}