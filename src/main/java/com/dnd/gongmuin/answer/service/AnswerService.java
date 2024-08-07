package com.dnd.gongmuin.answer.service;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.dto.AnswerDetailResponse;
import com.dnd.gongmuin.answer.dto.AnswerMapper;
import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.exception.AnswerErrorCode;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnswerService {

	private final QuestionPostRepository questionPostRepository;
	private final AnswerRepository answerRepository;

	@Transactional
	public AnswerDetailResponse registerAnswer(
		Long questionPostId,
		RegisterAnswerRequest request,
		Member member
	) {
		QuestionPost questionPost = findQuestionPostById(questionPostId);
		Answer answer = AnswerMapper.toAnswer(questionPostId, questionPost.isQuestioner(member), request, member);
		return AnswerMapper.toAnswerDetailResponse(answerRepository.save(answer));
	}

	@Transactional(readOnly = true)
	public PageResponse<AnswerDetailResponse> getAnswersByQuestionPostId(Long questionPostId) {
		validateIfQuestionPostExists(questionPostId);
		Slice<AnswerDetailResponse> answerResponsePage = answerRepository
			.findByQuestionPostId(questionPostId)
			.map(AnswerMapper::toAnswerDetailResponse);
		return PageMapper.toPageResponse(answerResponsePage);
	}

	@Transactional
	public AnswerDetailResponse chooseAnswer(
		Long answerId,
		Member member
	) {
		Answer answer = findAnswerById(answerId);
		QuestionPost questionPost = findQuestionPostById(answer.getQuestionPostId());
		validateIfQuestioner(member, questionPost);
		questionPost.chooseAnswer(answer);
		return AnswerMapper.toAnswerDetailResponse(answer);
	}

	private static void validateIfQuestioner(Member member, QuestionPost questionPost) {
		if (!questionPost.isQuestioner(member)) {
			throw new ValidationException(QuestionPostErrorCode.NOT_AUTHORIZED);
		}
	}

	private void validateIfQuestionPostExists(Long questionPostId) {
		boolean isExists = questionPostRepository.existsById(questionPostId);
		if (!isExists) {
			throw new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST);
		}
	}

	private Answer findAnswerById(Long answerId) {
		return answerRepository.findById(answerId)
			.orElseThrow(() -> new NotFoundException(AnswerErrorCode.NOT_FOUND_ANSWER));
	}

	private QuestionPost findQuestionPostById(Long questionPostId) {
		return questionPostRepository.findById(questionPostId)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
	}
}