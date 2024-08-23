package com.dnd.gongmuin.member.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.fixture.CreditHistoryFixture;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.UpdateMemberProfileRequest;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[MemberController] 통합테스트")
class MemberControllerTest extends ApiTestSupport {

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
	CreditHistoryRepository creditHistoryRepository;

	@AfterEach
	void tearDown() {
		creditHistoryRepository.deleteAll();
		answerRepository.deleteAll();
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
	}

	@DisplayName("로그인 된 사용자 프로필 정보를 조회한다.")
	@Test
	void getMemberProfile() throws Exception {
		// when  // then
		mockMvc.perform(get("/api/members/profile")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("nickname").value("김회원"))
			.andExpect(jsonPath("jobGroup").value("공업"))
			.andExpect(jsonPath("jobCategory").value("가스"))
			.andExpect(jsonPath("credit").value(10000));
	}

	@DisplayName("로그인 된 사용자 프로필 정보를 수정한다.")
	@Test
	void updateMemberProfile() throws Exception {
		// given
		UpdateMemberProfileRequest request = new UpdateMemberProfileRequest("박회원", "행정", "가스");

		// when  // then
		mockMvc.perform(patch("/api/members/profile/edit")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("nickname").value("박회원"))
			.andExpect(jsonPath("jobGroup").value("행정"))
			.andExpect(jsonPath("jobCategory").value("가스"))
			.andExpect(jsonPath("credit").value(10000));
	}

	@DisplayName("로그인 된 회원이 작성한 질문을 전체 조회한다.")
	@Test
	void getQuestionPostsByMember() throws Exception {
		// given
		Member member = MemberFixture.member2();
		Member savedMember = memberRepository.save(member);

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(loginMember, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(savedMember, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(loginMember, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		InteractionCount interactionCount1 = InteractionCountFixture.interactionCount(InteractionType.SAVED,
			questionPost1.getId());
		InteractionCount interactionCount2 = InteractionCountFixture.interactionCount(InteractionType.RECOMMEND,
			questionPost1.getId());
		InteractionCount interactionCount3 = InteractionCountFixture.interactionCount(InteractionType.SAVED,
			questionPost3.getId());
		InteractionCount interactionCount4 = InteractionCountFixture.interactionCount(InteractionType.RECOMMEND,
			questionPost3.getId());

		ReflectionTestUtils.setField(interactionCount1, "id", 1L);
		ReflectionTestUtils.setField(interactionCount1, "count", 10);
		ReflectionTestUtils.setField(interactionCount2, "id", 2L);
		ReflectionTestUtils.setField(interactionCount2, "count", 20);
		ReflectionTestUtils.setField(interactionCount3, "id", 3L);
		ReflectionTestUtils.setField(interactionCount3, "count", 30);
		ReflectionTestUtils.setField(interactionCount4, "id", 4L);
		ReflectionTestUtils.setField(interactionCount4, "count", 40);

		interactionCountRepository.saveAll(
			List.of(interactionCount1, interactionCount2, interactionCount3, interactionCount4));

		// when  // then
		mockMvc.perform(get("/api/members/question-posts")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost3.getId()))
			.andExpect(jsonPath("$.content[0].bookmarkCount").value(30))
			.andExpect(jsonPath("$.content[0].recommendCount").value(40))
			.andExpect(jsonPath("$.content[1].questionPostId").value(questionPost1.getId()))
			.andExpect(jsonPath("$.content[1].bookmarkCount").value(10))
			.andExpect(jsonPath("$.content[1].recommendCount").value(20));
	}

	@DisplayName("로그인 된 회원이 댓글 단 질문을 전체 조회한다.")
	@Test
	void getAnsweredQuestionPostsByMember() throws Exception {
		// given
		Member member = MemberFixture.member2();
		Member savedMember = memberRepository.save(member);

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(loginMember, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(savedMember, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(loginMember, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Answer answer1 = AnswerFixture.answer(questionPost2.getId(), loginMember);
		Answer answer2 = AnswerFixture.answer(questionPost3.getId(), loginMember);
		Answer answer3 = AnswerFixture.answer(questionPost3.getId(), loginMember);
		answerRepository.saveAll(List.of(answer1, answer3));
		answerRepository.save(answer2);

		// when  // then
		mockMvc.perform(get("/api/members/question-posts/answers")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost3.getId()))
			.andExpect(jsonPath("$.content[0].answerId").value(answer2.getId()))
			.andExpect(jsonPath("$.content[1].questionPostId").value(questionPost2.getId()))
			.andExpect(jsonPath("$.content[1].answerId").value(answer1.getId()));
	}

	@DisplayName("로그인 된 회원의 스크랩 질문을 전체 조회한다.")
	@Test
	void getBookmarksByMember() throws Exception {
		// given
		Member member = MemberFixture.member2();
		Member savedMember = memberRepository.save(member);

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(loginMember, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(savedMember, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(loginMember, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Interaction interaction1 = InteractionFixture.interaction(InteractionType.SAVED, loginMember.getId(),
			questionPost1.getId());
		Interaction interaction3 = InteractionFixture.interaction(InteractionType.RECOMMEND, loginMember.getId(),
			questionPost2.getId());
		Interaction interaction2 = InteractionFixture.interaction(InteractionType.SAVED, loginMember.getId(),
			questionPost3.getId());
		Interaction interaction4 = InteractionFixture.interaction(InteractionType.RECOMMEND, loginMember.getId(),
			questionPost3.getId());
		interactionRepository.saveAll(List.of(interaction1, interaction2, interaction3, interaction4));

		// when  // then
		mockMvc.perform(get("/api/members/question-posts/bookmarks")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost3.getId()))
			.andExpect(jsonPath("$.content[1].questionPostId").value(questionPost1.getId()));
	}

	@DisplayName("회원의 전체 크레딧 내역을 조회한다.")
	@Test
	void getCreditHistoryByMember() throws Exception {
		// given
		Member member2 = MemberFixture.member2();
		memberRepository.save(member2);

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(loginMember);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member2);
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member2);
		Answer answer2 = AnswerFixture.answer(questionPost2.getId(), loginMember);
		answerRepository.saveAll(List.of(answer1, answer2));

		CreditHistory ch1 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost1.getMember());
		CreditHistory ch2 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer1.getMember());
		CreditHistory ch3 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost2.getMember());
		CreditHistory ch4 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer2.getMember());
		creditHistoryRepository.saveAll(List.of(ch1, ch2, ch3, ch4));

		// when  // then
		mockMvc.perform(get("/api/members/credit/histories?type=전체")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].id").value(ch4.getId()))
			.andExpect(jsonPath("$.content[0].type").value(ch4.getType().getLabel()))
			.andExpect(jsonPath("$.content[0].detail").value(ch4.getDetail()))
			.andExpect(jsonPath("$.content[0].amount").value(ch4.getAmount()))
			.andExpect(jsonPath("$.content[1].id").value(ch1.getId()))
			.andExpect(jsonPath("$.content[1].type").value(ch1.getType().getLabel()))
			.andExpect(jsonPath("$.content[1].detail").value(ch1.getDetail()))
			.andExpect(jsonPath("$.content[1].amount").value(ch1.getAmount()));
	}

	@DisplayName("회원의 전체 크레딧 출금 내역을 조회한다.")
	@Test
	void getCreditHistoryByMemberInWithdrawal() throws Exception {
		// given
		Member member2 = MemberFixture.member2();
		memberRepository.save(member2);

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(loginMember);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member2);
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member2);
		Answer answer2 = AnswerFixture.answer(questionPost2.getId(), loginMember);
		answerRepository.saveAll(List.of(answer1, answer2));

		CreditHistory ch1 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost1.getMember());
		CreditHistory ch2 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer1.getMember());
		CreditHistory ch3 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost2.getMember());
		CreditHistory ch4 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer2.getMember());
		creditHistoryRepository.saveAll(List.of(ch1, ch2, ch3, ch4));

		// when  // then
		mockMvc.perform(get("/api/members/credit/histories?type=출금")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(1))
			.andExpect(jsonPath("$.content[0].id").value(ch1.getId()))
			.andExpect(jsonPath("$.content[0].type").value(ch1.getType().getLabel()))
			.andExpect(jsonPath("$.content[0].detail").value(ch1.getDetail()))
			.andExpect(jsonPath("$.content[0].amount").value(ch1.getAmount()));
	}

	@DisplayName("회원의 전체 크레딧 입금 내역을 조회한다.")
	@Test
	void getCreditHistoryByMemberInDeposit() throws Exception {
		// given
		Member member2 = MemberFixture.member2();
		memberRepository.save(member2);

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(loginMember);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member2);
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member2);
		Answer answer2 = AnswerFixture.answer(questionPost2.getId(), loginMember);
		answerRepository.saveAll(List.of(answer1, answer2));

		CreditHistory ch1 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost1.getMember());
		CreditHistory ch2 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer1.getMember());
		CreditHistory ch3 = CreditHistoryFixture.creditHistory(CreditType.CHOOSE, questionPost1.getReward(),
			questionPost2.getMember());
		CreditHistory ch4 = CreditHistoryFixture.creditHistory(CreditType.CHOSEN, questionPost1.getReward(),
			answer2.getMember());
		creditHistoryRepository.saveAll(List.of(ch1, ch2, ch3, ch4));

		// when  // then
		mockMvc.perform(get("/api/members/credit/histories?type=입금")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(1))
			.andExpect(jsonPath("$.content[0].id").value(ch4.getId()))
			.andExpect(jsonPath("$.content[0].type").value(ch4.getType().getLabel()))
			.andExpect(jsonPath("$.content[0].detail").value(ch4.getDetail()))
			.andExpect(jsonPath("$.content[0].amount").value(ch4.getAmount()));
	}
}
