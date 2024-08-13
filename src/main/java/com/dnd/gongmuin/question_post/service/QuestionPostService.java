package com.dnd.gongmuin.question_post.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.QuestionPostMapper;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostQueryRepository;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionPostService {

	private final QuestionPostRepository questionPostRepository;
	private final QuestionPostQueryRepository questionPostQueryRepository;

	@Transactional
	public QuestionPostDetailResponse registerQuestionPost(RegisterQuestionPostRequest request, Member member) {
		if (member.getCredit() < request.reward()) {
			throw new ValidationException(MemberErrorCode.NOT_ENOUGH_CREDIT);
		}
		QuestionPost questionPost = QuestionPostMapper.toQuestionPost(request, member);
		return QuestionPostMapper.toQuestionPostDetailResponse(questionPostRepository.save(questionPost));
	}

	@Transactional(readOnly = true)
	public QuestionPostDetailResponse getQuestionPostById(Long questionPostId) {
		QuestionPost questionPost = questionPostRepository.findById(questionPostId)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
		return QuestionPostMapper.toQuestionPostDetailResponse(questionPost);
	}

	@Transactional(readOnly = true)
	public PageResponse<QuestionPostSimpleResponse> searchQuestionPost(
		QuestionPostSearchCondition condition,
		Pageable pageable
	) {
		Slice<QuestionPostSimpleResponse> responsePage = questionPostQueryRepository
			.searchQuestionPosts(condition, pageable)
			.map(QuestionPostMapper::toQuestionPostSimpleResponse);
		return PageMapper.toPageResponse(responsePage);
	}
}
