package com.dnd.gongmuin.question_post.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.service.CreditHistoryService;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;
import com.dnd.gongmuin.question_post.domain.QuestionPostStatus;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.request.UpdateQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.DeleteQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostCreditCheckResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.RegisterQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.UpdateQuestionPostResponse;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostImageRepository;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[QuestionPostService 테스트]")
@ExtendWith(MockitoExtension.class)
class QuestionPostServiceTest {

	private final Member member = MemberFixture.member(1L);

	@Mock
	private QuestionPostRepository questionPostRepository;

	@Mock
	private QuestionPostImageRepository questionPostImageRepository;

	@Mock
	private InteractionRepository interactionRepository;

	@Mock
	private InteractionCountRepository interactionCountRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private AnswerRepository answerRepository;

	@Mock
	private CreditHistoryService creditHistoryService;

	@InjectMocks
	private QuestionPostService questionPostService;

	@DisplayName("[질문글을 등록할 수 있다.]")
	@Test
	void registerQuestionPost() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L);

		RegisterQuestionPostRequest request =
			new RegisterQuestionPostRequest(
				"제목",
				"내용",
				List.of("image1.jpg", "image2.jpg"),
				1000,
				"공업"
			);

		given(questionPostRepository.save(any(QuestionPost.class))).willReturn(questionPost);
		given(memberRepository.save(any(Member.class))).willReturn(member);
		doNothing().when(creditHistoryService).saveCreditHistory(
			CreditType.WRITE_QUESTION_POST,
			questionPost.getReward(),
			member
		);

		//when
		RegisterQuestionPostResponse response = questionPostService.registerQuestionPost(request, member);

		//then
		assertAll(
			() -> assertThat(response.title()).isEqualTo(request.title()),
			() -> assertThat(response.content()).isEqualTo(request.content()),
			() -> assertThat(response.reward()).isEqualTo(request.reward()),
			() -> assertThat(response.targetJobGroup()).isEqualTo(request.targetJobGroup()),
			() -> assertThat(response.status()).isEqualTo(QuestionPostStatus.ANSWER_WAITING.getStatus()),
			() -> assertThat(response.remainingCredit()).isEqualTo(member.getCredit())
		);
	}

	@DisplayName("[질문글 아이디로 질문글을 상세 조회할 수 있다. 상호작용 이력 존재x]")
	@Test
	void getQuestionPostById_noInteraction() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.SAVED))
			.willReturn(false);

		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.RECOMMEND))
			.willReturn(false);

		//when
		QuestionPostDetailResponse response
			= questionPostService.getQuestionPostById(questionPost.getId(), member);

		//then
		assertAll(
			() -> assertThat(response.questionPostId()).isEqualTo(questionPost.getId()),
			() -> assertThat(response.recommendCount()).isZero(),
			() -> assertThat(response.savedCount()).isZero(),
			() -> assertThat(response.status()).isEqualTo(QuestionPostStatus.ANSWER_WAITING.getStatus())
		);
	}

	@DisplayName("[질문글 아이디로 질문글을 상세 조회할 수 있다. 상호작용 이력 존재]")
	@Test
	void getQuestionPostById_interaction() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		InteractionCount recommendCount
			= InteractionCountFixture.interactionCount(InteractionType.RECOMMEND, questionPostId);
		InteractionCount savedCount
			= InteractionCountFixture.interactionCount(InteractionType.SAVED, questionPostId);

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.RECOMMEND
		)).willReturn(Optional.of(recommendCount));

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.SAVED
		)).willReturn(Optional.of(savedCount));

		//when
		QuestionPostDetailResponse response
			= questionPostService.getQuestionPostById(questionPost.getId(), member);
		//then
		assertAll(
			() -> assertThat(response.questionPostId()).isEqualTo(questionPost.getId()),
			() -> assertThat(response.recommendCount()).isEqualTo(recommendCount.getCount()).isEqualTo(1),
			() -> assertThat(response.savedCount()).isEqualTo(savedCount.getCount()).isEqualTo(1),
			() -> assertThat(response.status()).isEqualTo(QuestionPostStatus.ANSWER_WAITING.getStatus())
		);
	}

	@DisplayName("[질문글 아이디로 질문글을 상세 조회할 수 있다. 나의 추천 이력만 존재]")
	@Test
	void getQuestionPostById_my_interaction() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.SAVED))
			.willReturn(false);
		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.RECOMMEND))
			.willReturn(true);

		InteractionCount recommendCount
			= InteractionCountFixture.interactionCount(InteractionType.RECOMMEND, questionPostId);

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.SAVED
		)).willReturn(Optional.empty());

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.RECOMMEND
		)).willReturn(Optional.of(recommendCount));

		//when
		QuestionPostDetailResponse response
			= questionPostService.getQuestionPostById(questionPost.getId(), member);

		//then
		assertAll(
			() -> assertThat(response.questionPostId())
				.isEqualTo(questionPost.getId()),
			() -> assertThat(response.isSaved())
				.isFalse(),
			() -> assertThat(response.isRecommended())
				.isTrue(),
			() -> assertThat(response.questionPostId())
				.isEqualTo(questionPost.getId()),
			() -> assertThat(response.savedCount()).isZero(),
			() -> assertThat(response.recommendCount())
				.isEqualTo(recommendCount.getCount()).isEqualTo(1),
			() -> assertThat(response.status()).isEqualTo(QuestionPostStatus.ANSWER_WAITING.getStatus())
		);
	}

	@DisplayName("[질문글 업데이트를 할 수 있다.]")
	@Test
	void updateQuestionPost() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(member);
		UpdateQuestionPostRequest request =
			new UpdateQuestionPostRequest(
				questionPost.getTitle() + "ㅇㅇㅇ",
				questionPost.getContent(),
				null,
				questionPost.getReward() * 2,
				"행정"
			);

		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		//when
		UpdateQuestionPostResponse response
			= questionPostService.updateQuestionPost(questionPostId, request);

		//then
		assertAll(
			() -> assertThat(response.title())
				.isEqualTo(request.title()),
			() -> assertThat(response.reward())
				.isEqualTo(request.reward()),
			() -> assertThat(response.targetJobGroup())
				.isEqualTo(request.targetJobGroup()),
			() -> assertThat(response.imageUrls())
				.isEqualTo(questionPost.getImages().stream()
					.map(QuestionPostImage::getImageUrl).toList())
		);
	}

	@DisplayName("[질문글을 삭제할 수 있다.]")
	@Test
	void deleteQuestionPost() {
		//given
		Long questionPostId = 1L;
		int previousCredit = member.getCredit();
		QuestionPost questionPost = QuestionPostFixture.questionPost(member);

		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));
		given(answerRepository.existsByQuestionPostId(questionPostId)).willReturn(false);

		//when
		DeleteQuestionPostResponse response
			= questionPostService.deleteQuestionPost(questionPostId, member);

		//then
		assertThat(response.remainingCredit())
			.isEqualTo(previousCredit + questionPost.getReward());
	}

	@DisplayName("[답변이 존재하는 질문글은 삭제할 수 없다.]")
	@Test
	void deleteQuestionPostFails() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(member);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));
		given(answerRepository.existsByQuestionPostId(questionPostId)).willReturn(true);

		//when & then
		ValidationException exception = assertThrows(ValidationException.class,
			() -> questionPostService.deleteQuestionPost(questionPostId, member));

		assertThat(exception.getMessage())
			.isEqualTo(QuestionPostErrorCode.CAN_NOT_DELETE_QUESTION_POST.getMessage());
	}

	@DisplayName("[질문글 작성자가 아닌 경우 질문글을 삭제할 수 없다.]")
	@Test
	void deleteQuestionPostFails2() {
		//given
		Long questionPostId = 1L;
		Member unauthorizedMember = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(unauthorizedMember);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));
		given(answerRepository.existsByQuestionPostId(questionPostId)).willReturn(false);

		//when & then
		ValidationException exception = assertThrows(ValidationException.class,
			() -> questionPostService.deleteQuestionPost(questionPostId, member));

		assertThat(exception.getMessage())
			.isEqualTo(QuestionPostErrorCode.NOT_AUTHORIZED.getMessage());
	}

	@DisplayName("질문글을 작성하기 전 충분한 크레딧을 가지고 있는지 검증한다.")
	@Test
	void validateCreditBeforeRegisteringQuestionPost() {
		// given
		final int NOT_ENOUGH_CREDIT = 1_000;
		final int NOT_ENOUGH_CREDIT2 = 1_999;
		final int ENOUGH_CREDIT = 2_000;
		final int ENOUGH_CREDIT2 = 2_001;

		Member member1 = MemberFixture.member(1L);
		Member member2 = MemberFixture.member(2L);
		Member member3 = MemberFixture.member(3L);
		Member member4 = MemberFixture.member(4L);

		ReflectionTestUtils.setField(member1, "credit", NOT_ENOUGH_CREDIT);
		ReflectionTestUtils.setField(member2, "credit", NOT_ENOUGH_CREDIT2);
		ReflectionTestUtils.setField(member3, "credit", ENOUGH_CREDIT);
		ReflectionTestUtils.setField(member4, "credit", ENOUGH_CREDIT2);

		// when
		QuestionPostCreditCheckResponse response1 = questionPostService.checkQuestionPostCredit(member1);
		QuestionPostCreditCheckResponse response2 = questionPostService.checkQuestionPostCredit(member2);
		QuestionPostCreditCheckResponse response3 = questionPostService.checkQuestionPostCredit(member3);
		QuestionPostCreditCheckResponse response4 = questionPostService.checkQuestionPostCredit(member4);

		// then
		Assertions.assertAll(
			() -> assertFalse(response1.hasEnoughCredit()),
			() -> assertFalse(response2.hasEnoughCredit()),
			() -> assertTrue(response3.hasEnoughCredit()),
			() -> assertTrue(response4.hasEnoughCredit())
		);

	}
}
