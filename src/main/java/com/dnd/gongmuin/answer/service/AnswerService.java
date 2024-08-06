package com.dnd.gongmuin.answer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.dto.AnswerDetailResponse;
import com.dnd.gongmuin.answer.dto.AnswerMapper;
import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
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
		QuestionPost questionPost = questionPostRepository.findById(questionPostId).orElseThrow();
		boolean isQuestioner
			= questionPost.getMember().getId().equals(member.getId());
		Answer answer = AnswerMapper.toAnswer(questionPostId, isQuestioner, request, member);
		return AnswerMapper.toAnswerDetailResponse(answerRepository.save(answer));
	}

}
