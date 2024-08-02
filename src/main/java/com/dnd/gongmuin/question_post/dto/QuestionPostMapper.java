package com.dnd.gongmuin.question_post.dto;

import java.util.List;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionPostMapper {

	public static QuestionPost toQuestionPost(RegisterQuestionPostRequest request, Member member) {
		JobGroup jobGroup = JobGroup.of(request.targetJobGroup());
		List<QuestionPostImage> images =request.imageUrls().stream()
			.map(QuestionPostImage::from)
			.toList();
		return QuestionPost.of(request.title(), request.content(), 1000, jobGroup, images, member);
	}

	public static QuestionPostDetailResponse toQuestionPostDetailResponse(QuestionPost questionPost) {
		Member member = questionPost.getMember();
		return QuestionPostDetailResponse.of(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getImages().stream()
				.map(QuestionPostImage::getImageUrl).toList(),
			questionPost.getReward(),
			questionPost.getJobGroup().getLabel(),
			member.getId(),
			member.getNickname(),
			member.getJobGroup().getLabel()
		);
	}
}
