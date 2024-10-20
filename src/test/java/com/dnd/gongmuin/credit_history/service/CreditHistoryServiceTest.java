package com.dnd.gongmuin.credit_history.service;

import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.CreditHistoryFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

@DisplayName("[AnswerService 테스트]")
@ExtendWith(MockitoExtension.class)
class CreditHistoryServiceTest {

	@Mock
	private CreditHistoryRepository creditHistoryRepository;

	@Mock
	private MemberRepository memberRepository;

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

	@DisplayName("회원 아이디 리스트에 속하는 회원에 대한 크레딧을 모두 저장할 수 있다.")
	@Test
	void test() {
		//given
		List<Long> memberIds = List.of(1L, 2L);
		Member member1 = MemberFixture.member(memberIds.get(0));
		Member member2 = MemberFixture.member(memberIds.get(1));

		given(memberRepository.findAllById(memberIds))
			.willReturn(List.of(member1, member2));

		List<CreditHistory> expectedHistories = Stream.of(member1, member2)
			.map(member -> CreditHistoryFixture.creditHistory(CreditType.CHAT_REFUND, 2000, member))
			.toList();

		//when
		creditHistoryService.saveCreditHistoryInMemberIds(
			memberIds,
			CreditType.CHAT_REFUND,
			2000
		);

		//then
		verify(creditHistoryRepository).saveAll(expectedHistories);
	}
}