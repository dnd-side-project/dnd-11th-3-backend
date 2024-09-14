package com.dnd.gongmuin.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.CreditHistoryFixture;
import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsResponse;
import com.dnd.gongmuin.member.dto.response.BookmarksResponse;
import com.dnd.gongmuin.member.dto.response.CreditHistoryResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsResponse;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

class MemberRepositoryTest extends DataJpaTestSupport {

	private final PageRequest pageRequest = PageRequest.of(0, 10);

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	QuestionPostRepository questionPostRepository;

	@Autowired
	AnswerRepository answerRepository;

	@Autowired
	InteractionCountRepository interactionCountRepository;

	@Autowired
	InteractionRepository interactionRepository;

	@Autowired
	private CreditHistoryRepository creditHistoryRepository;

	@AfterEach
	void tearDown() {
		creditHistoryRepository.deleteAll();
		answerRepository.deleteAll();
		interactionCountRepository.deleteAll();
		questionPostRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@DisplayName("소셜이메일로 특정 회원을 조회한다.")
	@Test
	void findMemberBySocialEmail() {
		// given
		Member member = MemberFixture.member();
		Member savedMember = memberRepository.save(member);

		// when
		Member findMember = memberRepository.findBySocialEmail(savedMember.getSocialEmail()).get();

		// then
		assertThat(findMember.getNickname()).isEqualTo(savedMember.getNickname());
	}

	@DisplayName("자신이 작성한 질문 목록만 조회할 수 있다.[상호작용 수 비포함]")
	@Test
	void getQuestionPostsByMember() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		// when
		Slice<QuestionPostsResponse> postsByMember = memberRepository.getQuestionPostsByMember(member1,
			pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(2),
			() -> assertThat(postsByMember).extracting(QuestionPostsResponse::questionTitle)
				.containsExactly(
					questionPost2.getTitle(),
					questionPost1.getTitle()
				),
			() -> assertThat(postsByMember).extracting(QuestionPostsResponse::questionPostId)
				.containsExactly(
					questionPost2.getId(),
					questionPost1.getId()
				)
		);
	}

	@DisplayName("자신이 작성한 질문 목록만 조회할 수 있다.[상호작용 수 포함]")
	@Test
	void getQuestionPostsByMemberWithInteractionCount() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		InteractionCount interactionCount1 = InteractionCountFixture.interactionCount(InteractionType.SAVED,
			questionPost1.getId());
		InteractionCount interactionCount2 = InteractionCountFixture.interactionCount(InteractionType.RECOMMEND,
			questionPost1.getId());
		ReflectionTestUtils.setField(interactionCount1, "id", 1L);
		ReflectionTestUtils.setField(interactionCount1, "count", 10);
		ReflectionTestUtils.setField(interactionCount2, "id", 2L);
		ReflectionTestUtils.setField(interactionCount2, "count", 20);
		interactionCountRepository.saveAll(List.of(interactionCount1, interactionCount2));

		// when
		Slice<QuestionPostsResponse> postsByMember = memberRepository.getQuestionPostsByMember(member1,
			pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(2),
			() -> assertThat(postsByMember).extracting(QuestionPostsResponse::questionPostId)
				.containsExactly(
					questionPost2.getId(),
					questionPost1.getId()
				),
			() -> assertThat(postsByMember).extracting(QuestionPostsResponse::questionTitle)
				.containsExactly(
					questionPost2.getTitle(),
					questionPost1.getTitle()
				),
			() -> assertThat(postsByMember).extracting(QuestionPostsResponse::bookmarkCount)
				.containsExactly(
					0,
					10
				),
			() -> assertThat(postsByMember).extracting(QuestionPostsResponse::recommendCount)
				.containsExactly(
					0,
					20
				)
		);
	}

	@DisplayName("자신이 댓글 단 질문 목록만 조회할 수 있다.[상호작용 수 미포함]")
	@Test
	void getAnsweredQuestionPostsByMember() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member1);
		Answer answer2 = AnswerFixture.answer(questionPost1.getId(), member2);
		Answer answer3 = AnswerFixture.answer(questionPost2.getId(), member2);
		Answer answer4 = AnswerFixture.answer(questionPost3.getId(), member1);
		answerRepository.saveAll(List.of(answer1, answer2, answer3, answer4));

		// when
		Slice<AnsweredQuestionPostsResponse> postsByMember =
			memberRepository.getAnsweredQuestionPostsByMember(member1, pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(2),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::questionTitle)
				.containsExactly(
					questionPost3.getTitle(),
					questionPost1.getTitle()
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::questionPostId)
				.containsExactly(
					questionPost3.getId(),
					questionPost1.getId()
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::answerId)
				.containsExactly(
					answer4.getId(),
					answer1.getId()
				)
		);
	}

	@DisplayName("자신이 댓글 단 질문 목록만 조회할 수 있다.[상호작용 수 포함]")
	@Test
	void getAnsweredQuestionPostsByMemberWithInteractionCount() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member1);
		Answer answer2 = AnswerFixture.answer(questionPost1.getId(), member2);
		Answer answer3 = AnswerFixture.answer(questionPost2.getId(), member2);
		Answer answer4 = AnswerFixture.answer(questionPost3.getId(), member1);
		answerRepository.saveAll(List.of(answer1, answer2, answer3, answer4));

		InteractionCount interactionCount1 = InteractionCountFixture.interactionCount(InteractionType.SAVED,
			questionPost1.getId());
		InteractionCount interactionCount2 = InteractionCountFixture.interactionCount(InteractionType.RECOMMEND,
			questionPost1.getId());
		ReflectionTestUtils.setField(interactionCount1, "id", 1L);
		ReflectionTestUtils.setField(interactionCount1, "count", 10);
		ReflectionTestUtils.setField(interactionCount2, "id", 2L);
		ReflectionTestUtils.setField(interactionCount2, "count", 20);
		interactionCountRepository.saveAll(List.of(interactionCount1, interactionCount2));

		// when
		Slice<AnsweredQuestionPostsResponse> postsByMember =
			memberRepository.getAnsweredQuestionPostsByMember(member1, pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(2),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::questionTitle)
				.containsExactly(
					questionPost3.getTitle(),
					questionPost1.getTitle()
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::questionPostId)
				.containsExactly(
					questionPost3.getId(),
					questionPost1.getId()
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::answerId)
				.containsExactly(
					answer4.getId(),
					answer1.getId()
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::bookmarkCount)
				.containsExactly(
					0,
					10
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::recommendCount)
				.containsExactly(
					0,
					20
				)
		);
	}

	@DisplayName("답변단 게시글이 존재하지 않으면 질문 목록의 Size는 0 이다.")
	@Test
	void whenNoAnsweredQuestionPosts_thenGetQuestionPosts() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member1);
		Answer answer2 = AnswerFixture.answer(questionPost2.getId(), member1);
		Answer answer3 = AnswerFixture.answer(questionPost3.getId(), member1);
		answerRepository.saveAll(List.of(answer1, answer2, answer3));

		InteractionCount interactionCount1 = InteractionCountFixture.interactionCount(InteractionType.SAVED,
			questionPost1.getId());
		InteractionCount interactionCount2 = InteractionCountFixture.interactionCount(InteractionType.RECOMMEND,
			questionPost1.getId());
		ReflectionTestUtils.setField(interactionCount1, "id", 1L);
		ReflectionTestUtils.setField(interactionCount1, "count", 10);
		ReflectionTestUtils.setField(interactionCount2, "id", 2L);
		ReflectionTestUtils.setField(interactionCount2, "count", 20);
		interactionCountRepository.saveAll(List.of(interactionCount1, interactionCount2));

		// when
		Slice<AnsweredQuestionPostsResponse> postsByMember =
			memberRepository.getAnsweredQuestionPostsByMember(member2, pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(0),
			() -> assertThat(postsByMember.getContent()).isEmpty()
		);
	}

	@DisplayName("답변 단 질문을 가져올 때, 질문 내 답변이 여러 개면 가장 마지막 작성된 답변을 가져온다")
	@Test
	void whenAnsweredQuestionPosts_thenGetQuestionPostsAtRecently() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.22");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member1, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member1);
		Answer answer2 = AnswerFixture.answer(questionPost1.getId(), member1);
		Answer answer3 = AnswerFixture.answer(questionPost1.getId(), member2);
		ReflectionTestUtils.setField(answer1, "content", "1번답변.");
		ReflectionTestUtils.setField(answer2, "content", "2번답변.");
		answerRepository.saveAll(List.of(answer1, answer3));
		answerRepository.save(answer2);

		// when
		Slice<AnsweredQuestionPostsResponse> postsByMember =
			memberRepository.getAnsweredQuestionPostsByMember(member1, pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(1),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::answerId)
				.containsExactly(
					answer2.getId()
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::answerCreatedAt)
				.containsExactly(
					answer2.getUpdatedAt().toString()
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsResponse::answerContent)
				.containsExactly(
					answer2.getContent()
				)
		);
	}

	@DisplayName("회원의 스크랩 질문 목록을 조회힌다.")
	@Test
	void getBookmarksByMember() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member2, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Interaction interaction1 = InteractionFixture.interaction(InteractionType.SAVED, member1.getId(),
			questionPost1.getId());
		Interaction interaction2 = InteractionFixture.interaction(InteractionType.SAVED, member1.getId(),
			questionPost2.getId());
		Interaction interaction3 = InteractionFixture.interaction(InteractionType.RECOMMEND, member1.getId(),
			questionPost2.getId());
		Interaction interaction4 = InteractionFixture.interaction(InteractionType.RECOMMEND, member1.getId(),
			questionPost3.getId());
		interactionRepository.saveAll(List.of(interaction1, interaction2, interaction3, interaction4));

		// when
		Slice<BookmarksResponse> bookmarksByMember = memberRepository.getBookmarksByMember(member1,
			pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(bookmarksByMember).hasSize(2),
			() -> assertThat(bookmarksByMember).extracting(BookmarksResponse::questionPostId)
				.containsExactly(
					questionPost2.getId(),
					questionPost1.getId()
				),
			() -> assertThat(bookmarksByMember).extracting(BookmarksResponse::questionTitle)
				.containsExactly(
					questionPost2.getTitle(),
					questionPost1.getTitle()
				)
		);
	}

	@DisplayName("회원의 전체 크리뎃 내역을 조회한다.")
	@Test
	void getCreditHistoryByMember() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1);
		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member2);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member2);
		Answer answer2 = AnswerFixture.answer(questionPost2.getId(), member1);

		CreditHistory ch1 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost1.getMember());
		CreditHistory ch2 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer1.getMember());
		CreditHistory ch3 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost2.getMember());
		CreditHistory ch4 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer2.getMember());
		creditHistoryRepository.saveAll(List.of(ch1, ch2, ch3, ch4));

		// when
		Slice<CreditHistoryResponse> creditHistoryByMember = memberRepository.getCreditHistoryByMember(
			null,
			member1,
			pageRequest
		);

		// then
		Assertions.assertAll(
			() -> assertThat(creditHistoryByMember).hasSize(2),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::id)
				.containsExactly(
					ch4.getId(),
					ch1.getId()
				),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::type)
				.containsExactly(
					ch4.getType().getLabel(),
					ch1.getType().getLabel()
				),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::detail)
				.containsExactly(
					ch4.getDetail(),
					ch1.getDetail()
				),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::profileImageNo)
				.satisfies(profileImageNos -> {
					Integer profileImageNo1 = profileImageNos.get(0);
					Integer profileImageNo2 = profileImageNos.get(1);

					assertThat(profileImageNo1).isEqualTo(ch4.getMember().getProfileImageNo());
					assertThat(profileImageNo2).isBetween(1, 9);
				})
		);
	}

	@DisplayName("회원의 전체 크레딧 출금 내역을 조회한다.")
	@Test
	void getCreditHistoryByMemberInWithdrawal() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1);
		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member2);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member2);
		Answer answer2 = AnswerFixture.answer(questionPost2.getId(), member1);

		CreditHistory ch1 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost1.getMember());
		CreditHistory ch2 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer1.getMember());
		CreditHistory ch3 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost2.getMember());
		CreditHistory ch4 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer2.getMember());
		List<CreditHistory> creditHistories = creditHistoryRepository.saveAll(List.of(ch1, ch2, ch3, ch4));
		for (CreditHistory ch : creditHistories) {
			System.out.println("ch.getId() = " + ch.getId());
			System.out.println("ch.getType().getDetail() = " + ch.getType().getDetail());
		}

		// when
		Slice<CreditHistoryResponse> creditHistoryByMember = memberRepository.getCreditHistoryByMember(
			"출금",
			member1,
			pageRequest
		);

		// then
		Assertions.assertAll(
			() -> assertThat(creditHistoryByMember).hasSize(1),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::id)
				.containsExactly(
					ch1.getId()
				),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::type)
				.containsExactly(
					ch1.getType().getLabel()
				),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::detail)
				.containsExactly(
					ch1.getDetail()
				)
		);
	}

	@DisplayName("회원의 전체 크리뎃 입금 내역을 조회한다.")
	@Test
	void getCreditHistoryByMemberInDeposit() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1);
		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member2);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member2);
		Answer answer2 = AnswerFixture.answer(questionPost2.getId(), member1);

		CreditHistory ch1 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost1.getMember());
		CreditHistory ch2 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer1.getMember());
		CreditHistory ch3 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost2.getMember());
		CreditHistory ch4 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer2.getMember());
		creditHistoryRepository.saveAll(List.of(ch1, ch2, ch3, ch4));

		// when
		Slice<CreditHistoryResponse> creditHistoryByMember = memberRepository.getCreditHistoryByMember(
			"입금",
			member1,
			pageRequest
		);

		// then
		Assertions.assertAll(
			() -> assertThat(creditHistoryByMember).hasSize(1),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::id)
				.containsExactly(
					ch4.getId()
				),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::type)
				.containsExactly(
					ch4.getType().getLabel()
				),
			() -> assertThat(creditHistoryByMember).extracting(CreditHistoryResponse::detail)
				.containsExactly(
					ch4.getDetail()
				)
		);
	}
}