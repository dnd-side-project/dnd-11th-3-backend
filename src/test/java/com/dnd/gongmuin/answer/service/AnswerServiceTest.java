package com.dnd.gongmuin.answer.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
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
import com.dnd.gongmuin.credit_history.service.CreditHistoryService;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.notification.service.NotificationService;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostStatus;
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

	@Mock
	private CreditHistoryService creditHistoryService;

	@Mock
	private NotificationService notificationService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private AnswerService answerService;

	@DisplayName("[답변을 등록할 수 있다.]")
	@Test
	void registerAnswer() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L);
		Answer answer = AnswerFixture.answer(1L, questionPost.getId());
		RegisterAnswerRequest request =
			new RegisterAnswerRequest("답변 내용");

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(answerRepository.save(any(Answer.class)))
			.willReturn(answer);

		//when
		AnswerDetailResponse response
			= answerService.registerAnswer(questionPost.getId(), request, MemberFixture.member(1L));

		//then
		Assertions.assertThat(response.content()).isEqualTo(request.content());
		Assertions.assertThat(questionPost.getQuestionPostStatus()).isEqualTo(QuestionPostStatus.CHOSEN_WAITING);
	}

	@DisplayName("[답변대기 상태가 아닌 질문글에 답변을 등록할 때 질문글 상태가 변하지 않는다.]")
	@Test
	void notChangeQuestionPostStatusWhenRegisterAnswerAndQuestionPostStatusIsNotAnswerWaiting() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L);
		ReflectionTestUtils.setField(questionPost, "questionPostStatus", QuestionPostStatus.CHOSEN_COMPLETE);
		Answer answer = AnswerFixture.answer(1L, questionPost.getId());
		RegisterAnswerRequest request =
			new RegisterAnswerRequest("답변 내용");

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(answerRepository.save(any(Answer.class)))
			.willReturn(answer);

		//when
		AnswerDetailResponse response
			= answerService.registerAnswer(questionPost.getId(), request, MemberFixture.member(1L));

		//then
		Assertions.assertThat(response.content()).isEqualTo(request.content());
		Assertions.assertThat(questionPost.getQuestionPostStatus()).isEqualTo(QuestionPostStatus.CHOSEN_COMPLETE);
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
		assertAll(
			() -> assertThat(response.content()).hasSize(2),
			() -> assertThat(response.hasNext()).isFalse(),
			() -> assertThat(response.content().get(0).answerId()).isEqualTo(answer1.getId())
		);
	}

	@DisplayName("[답변을 채택할 수 있다.]")
	@Test
	void chooseAnswer() {
		//given
		Long questionPostId = 1L;
		Member member = MemberFixture.member(1L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId, member);
		Answer answer = AnswerFixture.answer(1L, questionPostId);

		given(answerRepository.findByIdWithMember(answer.getId()))
			.willReturn(Optional.of(answer));
		given(questionPostRepository.findByIdWithMember(questionPost.getId()))
			.willReturn(Optional.of(questionPost));

		//when
		AnswerDetailResponse response = answerService.chooseAnswer(answer.getId(), member);

		//then
		Assertions.assertThat(response.isChosen()).isTrue();
	}

	@Disabled
	@DisplayName("[크레딧이 부족하면 답변을 채택할 수 없다.]")
	@Test
	void chooseAnswer_fail() {
		//given
		Long questionPostId = 1L;
		Member member = MemberFixture.member(1L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId, member);
		ReflectionTestUtils.setField(questionPost, "reward", member.getCredit() + 1);
		Answer answer = AnswerFixture.answer(1L, questionPostId);

		given(answerRepository.findByIdWithMember(answer.getId()))
			.willReturn(Optional.of(answer));
		given(questionPostRepository.findByIdWithMember(questionPost.getId()))
			.willReturn(Optional.of(questionPost));

		//when & then
		assertThatThrownBy(() -> answerService.chooseAnswer(answer.getId(), member))
			.isInstanceOf(ValidationException.class)
			.hasMessageContaining(MemberErrorCode.NOT_ENOUGH_CREDIT.getMessage());

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

		given(answerRepository.findByIdWithMember(answer.getId()))
			.willReturn(Optional.of(answer));
		given(questionPostRepository.findByIdWithMember(questionPost.getId()))
			.willReturn(Optional.of(questionPost));

		//when & then
		assertThatThrownBy(() -> answerService.chooseAnswer(answer.getId(), notQuestioner))
			.isInstanceOf(ValidationException.class)
			.hasMessageContaining(QuestionPostErrorCode.NOT_AUTHORIZED.getMessage());
	}

	@Disabled
	@DisplayName("[동시에 10_000개의 채택이 일어나 크레딧을 입금 받는다.]")
	@Test
	void creditHistoryWithOneHundred() throws Exception {
		// given
		final long threadCount = 10_000L;
		final int writerCredit = 10_000_000;
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		CountDownLatch latch = new CountDownLatch((int)threadCount);

		Member writer = MemberFixture.member(1L);
		Member answer = MemberFixture.member(2L);
		ReflectionTestUtils.setField(writer, "credit", writerCredit);
		ReflectionTestUtils.setField(answer, "credit", 0);

		List<QuestionPost> questionPosts = new ArrayList<>();
		List<Answer> answers = new ArrayList<>();

		for (long i = 1L; i <= threadCount; i++) {
			QuestionPost questionPost = QuestionPostFixture.questionPost(i, writer);

			Answer answer1 = AnswerFixture.answer(questionPost.getId(), answer);
			ReflectionTestUtils.setField(answer1, "id", i);
			questionPosts.add(questionPost);
			answers.add(answer1);

			given(answerRepository.findByIdWithMember(i)).willReturn(Optional.of(answer1));
			given(questionPostRepository.findByIdWithMember(questionPost.getId()))
				.willReturn(Optional.of(questionPost));
		}

		// when
		long startTime = System.currentTimeMillis();
		for (long i = 0L; i < threadCount; i++) {
			final int index = (int)i;
			executorService.submit(() -> {
				try {
					answerService.chooseAnswer(answers.get(index).getId(), writer);
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		long endTime = System.currentTimeMillis();
		System.out.println("Execution time: " + (endTime - startTime) + " ms");

		// then
		assertEquals(answer.getCredit(), writerCredit);
	}
}