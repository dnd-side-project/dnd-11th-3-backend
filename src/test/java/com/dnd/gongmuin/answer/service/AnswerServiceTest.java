package com.dnd.gongmuin.answer.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.dto.AnswerDetailResponse;
import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[AnswerService 테스트]")
@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

	private final Pageable pageRequest = PageRequest.of(0, 5);

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
		Long questionPostId = 1L;
		Answer answer = AnswerFixture.answer(1L, questionPostId);
		RegisterAnswerRequest request =
			RegisterAnswerRequest.from("답변 내용");

		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(QuestionPostFixture.questionPost(questionPostId)));
		given(answerRepository.save(any(Answer.class)))
			.willReturn(answer);

		//when
		AnswerDetailResponse response
			= answerService.registerAnswer(questionPostId, request, MemberFixture.member(1L));

		//then
		Assertions.assertThat(response.content()).isEqualTo(request.content());
	}

	@DisplayName("[질문글 아이디로 답변을 모두 조회할 수 있다.]")
	@Test
	void getAnswerByQuestionPostId() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId);
		Answer answer1 = AnswerFixture.answer(1L, questionPostId);
		Answer answer2 = AnswerFixture.answer(2L, questionPostId);

		given(questionPostRepository.existsById(questionPost.getId()))
			.willReturn(true);
		given(answerRepository.findByQuestionPostId(questionPostId))
			.willReturn(new SliceImpl<>(List.of(answer1, answer2), pageRequest, false));

		//when
		PageResponse<AnswerDetailResponse> response = answerService.getAnswersByQuestionPostId(
			questionPostId);

		//then
		Assertions.assertThat(response.content()).hasSize(2);
		Assertions.assertThat(response.hasNext()).isFalse();
		Assertions.assertThat(response.content().get(0).answerId()).isEqualTo(answer1.getId());
	}

	@DisplayName("[답변을 채택할 수 있다.]")
	@Test
	void chooseAnswer() {
		//given
		Long questionPostId = 1L;
		Member member = MemberFixture.member(1L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId, member);
		Answer answer = AnswerFixture.answer(1L, questionPostId);

		given(answerRepository.findById(answer.getId()))
			.willReturn(Optional.of(answer));
		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));

		//when
		AnswerDetailResponse response = answerService.chooseAnswer(answer.getId(), member);

		//then
		Assertions.assertThat(response.isChosen()).isTrue();
	}

	@DisplayName("[크레딧이 부족하면 답변을 채택할 수 없다.]")
	@Test
	void chooseAnswer_fail() {
		//given
		Long questionPostId = 1L;
		Member member = MemberFixture.member(1L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId, member);
		ReflectionTestUtils.setField(questionPost, "reward", member.getCredit() + 1);
		Answer answer = AnswerFixture.answer(1L, questionPostId);

		given(answerRepository.findById(answer.getId()))
			.willReturn(Optional.of(answer));
		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));

		//when & then
		assertThatThrownBy(() -> answerService.chooseAnswer(answer.getId(), member))
			.isInstanceOf(ValidationException.class)
			.hasMessageContaining(QuestionPostErrorCode.NOT_ENOUGH_CREDIT.getMessage());

	}

	@DisplayName("[질문자가 아니면 채택할 수 없다.]")
	@Test
	void chooseAnswer_fail2() {
		//given
		Long questionPostId = 1L;
		Member questioner = MemberFixture.member(1L);
		Member notQuestioner = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId, questioner);
		Answer answer = AnswerFixture.answer(1L, questionPostId);

		given(answerRepository.findById(answer.getId()))
			.willReturn(Optional.of(answer));
		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));

		//when & then
		assertThatThrownBy(() -> answerService.chooseAnswer(answer.getId(), notQuestioner))
			.isInstanceOf(ValidationException.class)
			.hasMessageContaining(QuestionPostErrorCode.NOT_AUTHORIZED.getMessage());
	}
}