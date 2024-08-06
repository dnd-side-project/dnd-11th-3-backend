package com.dnd.gongmuin.answer.service;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.dto.AnswerDetailResponse;
import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[AnswerService 테스트]")
@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

	@Mock
	private QuestionPostRepository questionPostRepository;

	@Mock
	private AnswerRepository answerRepository;

	@InjectMocks
	private AnswerService answerService;

	@DisplayName("[답변을 등록할 수 있다.]")
	@Test
	void registerAnswer() {
		//given
		Answer answer = AnswerFixture.answer(1L);
		RegisterAnswerRequest request =
			RegisterAnswerRequest.from("답변 내용");

		given(questionPostRepository.findById(1L))
			.willReturn(Optional.of(QuestionPostFixture.questionPost(1L)));
		given(answerRepository.save(any(Answer.class)))
			.willReturn(answer);

		//when
		AnswerDetailResponse response
			= answerService.registerAnswer(1L, request, MemberFixture.member(1L));

		//then
		Assertions.assertThat(response.content()).isEqualTo(request.content());
	}
}