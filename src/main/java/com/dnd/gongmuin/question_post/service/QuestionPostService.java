package com.dnd.gongmuin.question_post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.QuestionPostMapper;
import com.dnd.gongmuin.question_post.dto.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionPostService {

	private final QuestionPostRepository questionPostRepository;

	@Transactional
	public QuestionPostDetailResponse registerQuestionPost(@Valid RegisterQuestionPostRequest request, Member member) {
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
}
