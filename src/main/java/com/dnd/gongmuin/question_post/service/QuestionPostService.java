package com.dnd.gongmuin.question_post.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.QuestionPostMapper;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.request.UpdateQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;
import com.dnd.gongmuin.question_post.dto.response.RecQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.RegisterQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.UpdateQuestionPostResponse;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostImageRepository;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionPostService {

	private final QuestionPostRepository questionPostRepository;
	private final InteractionRepository interactionRepository;
	private final InteractionCountRepository interactionCountRepository;
	private final QuestionPostImageRepository questionPostImageRepository;

	private static void updateQuestionPost(UpdateQuestionPostRequest request, QuestionPost questionPost) {
		questionPost.updateQuestionPost(
			request.title(),
			request.content(),
			request.reward(),
			JobGroup.from(request.targetJobGroup())
		);
	}

	@Transactional
	public RegisterQuestionPostResponse registerQuestionPost(
		RegisterQuestionPostRequest request,
		Member member
	) {
		if (member.getCredit() < request.reward()) {
			throw new ValidationException(MemberErrorCode.NOT_ENOUGH_CREDIT);
		}
		QuestionPost questionPost = QuestionPostMapper.toQuestionPost(request, member);
		return QuestionPostMapper.toRegisterQuestionPostResponse(
			questionPostRepository.save(questionPost)
		);
	}

	@Transactional(readOnly = true)
	public QuestionPostDetailResponse getQuestionPostById(Long questionPostId, Member member) {
		QuestionPost questionPost = questionPostRepository.findById(questionPostId)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
		return QuestionPostMapper.toQuestionPostDetailResponse(
			questionPost,
			getIsInteractedByType(questionPostId, member.getId(), InteractionType.SAVED),
			getIsInteractedByType(questionPostId, member.getId(), InteractionType.RECOMMEND),
			getCountByType(questionPostId, InteractionType.SAVED),
			getCountByType(questionPostId, InteractionType.RECOMMEND)
		);
	}

	@Transactional(readOnly = true)
	public PageResponse<QuestionPostSimpleResponse> searchQuestionPost(
		QuestionPostSearchCondition condition,
		Pageable pageable
	) {
		Slice<QuestionPostSimpleResponse> responsePage =
			questionPostRepository.searchQuestionPosts(condition, pageable);
		return PageMapper.toPageResponse(responsePage);
	}

	@Transactional(readOnly = true)
	public PageResponse<RecQuestionPostResponse> getRecommendQuestionPosts(
		Member member,
		Pageable pageable
	) {
		Slice<RecQuestionPostResponse> responsePage
			= questionPostRepository.getRecommendQuestionPosts(member.getJobGroup(), pageable);
		return PageMapper.toPageResponse(responsePage);
	}

	@Transactional
	public UpdateQuestionPostResponse updateQuestionPost(
		Long questionPostId,
		UpdateQuestionPostRequest request
	) {
		QuestionPost questionPost = questionPostRepository.findById(questionPostId)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
		updateQuestionPostImages(questionPost, request.imageUrls());
		updateQuestionPost(request, questionPost);
		return QuestionPostMapper.toUpdateQuestionPostResponse(questionPost);
	}

	private void updateQuestionPostImages(QuestionPost questionPost, List<String> imageUrls) {
		if (imageUrls != null) { // 수정 사항 존재
			deleteImages(questionPost); // 기존 이미지 객체 삭제 (새로 비우기 || 수정할 값 존재)
			if (!imageUrls.isEmpty()) { //수정할 값 담아보냄
				questionPost.updatePostImages(imageUrls);
			}
		}
	}

	private void deleteImages(QuestionPost questionPost) {
		questionPostImageRepository.deleteByQuestionPost(questionPost);
		questionPost.clearPostImages();
	}

	private boolean getIsInteractedByType(Long questionPostId, Long memberId, InteractionType type) {
		return interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, memberId, type);
	}

	private int getCountByType(Long questionPostId, InteractionType type) {
		return interactionCountRepository
			.findByQuestionPostIdAndType(questionPostId, type)
			.map(InteractionCount::getCount)
			.orElse(0);
	}
}
