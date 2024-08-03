package com.dnd.gongmuin.question_post.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.QuestionPostMapper;
import com.dnd.gongmuin.question_post.dto.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionPostService {

	private final QuestionPostRepository questionPostRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public QuestionPostDetailResponse registerQuestionPost(RegisterQuestionPostRequest request) {
		QuestionPost questionPost = QuestionPostMapper.toQuestionPost(request, getTempMember());
		return QuestionPostMapper.toQuestionPostDetailResponse(questionPostRepository.save(questionPost));
	}

	@Transactional(readOnly = true)
	public QuestionPostDetailResponse getQuestionPostById(Long questionPostId) {
		QuestionPost questionPost = questionPostRepository.findById(questionPostId)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
		return QuestionPostMapper.toQuestionPostDetailResponse(questionPost);
	}

	// TODO: 시큐리티 인증 객체로 대체
	public Member getTempMember() {
		return memberRepository.findById(3L).orElseThrow(ValidationException::new);
	}

}
