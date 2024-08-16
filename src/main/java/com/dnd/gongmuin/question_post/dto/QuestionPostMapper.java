package com.dnd.gongmuin.question_post.dto;

import java.util.List;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.MemberInfo;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;
import com.dnd.gongmuin.question_post.dto.response.RegisterQuestionPostResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionPostMapper {

	public static QuestionPost toQuestionPost(RegisterQuestionPostRequest request, Member member) {
		JobGroup jobGroup = JobGroup.from(request.targetJobGroup());
		List<QuestionPostImage> images = request.imageUrls().stream()
			.map(QuestionPostImage::from)
			.toList();
		return QuestionPost.of(request.title(), request.content(), request.reward(), jobGroup, images, member);
	}

	public static QuestionPostDetailResponse toQuestionPostDetailResponse(
		QuestionPost questionPost,
		int recommendCount,
		int savedCount
	) {
		Member member = questionPost.getMember();
		return new QuestionPostDetailResponse(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getImages().stream()
				.map(QuestionPostImage::getImageUrl).toList(),
			questionPost.getReward(),
			questionPost.getJobGroup().getLabel(),
			new MemberInfo(
				member.getId(),
				member.getNickname(),
				member.getJobGroup().getLabel()
			),
			recommendCount,
			savedCount,
			questionPost.getCreatedAt().toString()
		);
	}

	public static RegisterQuestionPostResponse toQuestionPostDetailResponse(
		QuestionPost questionPost
	) {
		Member member = questionPost.getMember();
		return new RegisterQuestionPostResponse(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getImages().stream()
				.map(QuestionPostImage::getImageUrl).toList(),
			questionPost.getReward(),
			questionPost.getJobGroup().getLabel(),
			new MemberInfo(
				member.getId(),
				member.getNickname(),
				member.getJobGroup().getLabel()
			),
			questionPost.getCreatedAt().toString()
		);
	}

	public static QuestionPostSimpleResponse toQuestionPostSimpleResponse(QuestionPost questionPost) {
		return new QuestionPostSimpleResponse(
			questionPost.getId(),
			questionPost.getTitle(),
			questionPost.getContent(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getReward(),
			questionPost.getCreatedAt().toString(),
			questionPost.getIsChosen()
		);
	}
}
