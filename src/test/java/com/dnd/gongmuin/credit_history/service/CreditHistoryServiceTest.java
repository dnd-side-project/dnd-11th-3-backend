package com.dnd.gongmuin.credit_history.service;

import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.credit_history.CreditHistory;
import com.dnd.gongmuin.credit_history.CreditType;
import com.dnd.gongmuin.credit_history.fixture.CreditHistoryFixture;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

@DisplayName("[AnswerService 테스트]")
@ExtendWith(MockitoExtension.class)
class CreditHistoryServiceTest {

	@Mock
	private CreditHistoryRepository creditHistoryRepository;

	@InjectMocks
	private CreditHistoryService creditHistoryService;

	@DisplayName("[크레딧 내역을 저장할 수 있다.]")
	@Test
	void saveChosenCreditHistory() {
		QuestionPost questionPost = QuestionPostFixture.questionPost(MemberFixture.member(1L));
		Answer answer = AnswerFixture.answer(questionPost.getId(), MemberFixture.member(2L));

		List<CreditHistory> creditHistories = List.of(
			CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost.getReward(), questionPost.getMember()),
			CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost.getReward(), answer.getMember())
		);
		given(creditHistoryRepository.saveAll(anyList())).willReturn(creditHistories);

		creditHistoryService.saveChosenCreditHistory(questionPost, answer);
	}
}