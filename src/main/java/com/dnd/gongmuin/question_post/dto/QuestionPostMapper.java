package com.dnd.gongmuin.question_post.dto;

import java.util.Collections;
import java.util.List;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.MemberInfo;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.RegisterQuestionPostResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionPostMapper {

	public static QuestionPost toQuestionPost(RegisterQuestionPostRequest request, Member member) {
		JobGroup jobGroup = JobGroup.from(request.targetJobGroup());
		List<QuestionPostImage> images = urlsToImages(request);
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
			imagesToUrls(questionPost.getImages()),
			questionPost.getReward(),
			questionPost.getJobGroup().getLabel(),
			new MemberInfo(
				member.getId(),
				member.getNickname(),
				member.getJobGroup().getLabel()
			),
			savedCount,
			recommendCount,
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
			imagesToUrls(questionPost.getImages()),
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

	private static List<QuestionPostImage> urlsToImages(RegisterQuestionPostRequest request) {
		List<QuestionPostImage> images = null;
		if (request.imageUrls() != null) {
			images = request.imageUrls().stream()
				.map(QuestionPostImage::from)
				.toList();
		}
		return images;
	}

	private static List<String> imagesToUrls(List<QuestionPostImage> images) {
		if (images == null)
			return Collections.emptyList();
		return images.stream()
			.map(QuestionPostImage::getImageUrl)
			.toList();
	}
}
