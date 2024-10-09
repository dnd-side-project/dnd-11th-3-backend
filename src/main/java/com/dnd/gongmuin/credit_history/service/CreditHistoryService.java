package com.dnd.gongmuin.credit_history.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.dto.CreditHistoryMapper;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditHistoryService {

	private final CreditHistoryRepository creditHistoryRepository;

	@Transactional
	public void saveChosenCreditHistory(QuestionPost questionPost, Answer answer) {
		creditHistoryRepository.saveAll(List.of(
			CreditHistoryMapper.toCreditHistory(CreditType.CHOSEN, questionPost.getReward(), answer.getMember()),
			CreditHistoryMapper.toCreditHistory(CreditType.CHOOSE, questionPost.getReward(), questionPost.getMember())
		));
	}

	@Transactional
	public void saveChatCreditHistory(CreditType creditType, Member member) {
		int chatCredit = 2000;
		creditHistoryRepository.save(
			CreditHistoryMapper.toCreditHistory(creditType, chatCredit, member)
		);
	}
}
