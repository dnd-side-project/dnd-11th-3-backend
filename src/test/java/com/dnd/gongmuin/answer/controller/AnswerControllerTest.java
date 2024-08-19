package com.dnd.gongmuin.answer.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[Answer 통합 테스트]")
class AnswerControllerTest extends ApiTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private AnswerRepository answerRepository;

	@Autowired
	private CreditHistoryRepository creditHistoryRepository;

	@AfterEach
	void teardown() {
		creditHistoryRepository.deleteAll();
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
		answerRepository.deleteAll();
	}

	@DisplayName("[타 유저가 답변을 등록할 수 있다.]")
	@Test
	void registerAnswerByOther() throws Exception {

		Member anotherMember = memberRepository.save(MemberFixture.member2());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(anotherMember));

		RegisterAnswerRequest request = new RegisterAnswerRequest("본문");
		mockMvc.perform(post("/api/question-posts/{questionPostId}/answers", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(request.content()))
			.andExpect(jsonPath("$.isChosen").value(false))
			.andExpect(jsonPath("$.isQuestioner").value(false)) //질문자!=답변자
			.andExpect(jsonPath("$.memberInfo.memberId").value(loginMember.getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(loginMember.getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(loginMember.getJobGroup().getLabel())
			);
	}

	@DisplayName("[질문자가 답변을 등록할 수 있다.]")
	@Test
	void registerAnswerByQuestioner() throws Exception {
		QuestionPost questionPost
			= questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));

		RegisterAnswerRequest request = new RegisterAnswerRequest("본문");
		mockMvc.perform(post("/api/question-posts/{questionPostId}/answers", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(request.content()))
			.andExpect(jsonPath("$.isChosen").value(false))
			.andExpect(jsonPath("$.isQuestioner").value(true)) // 질문자 == 답변자
			.andExpect(jsonPath("$.memberInfo.memberId").value(loginMember.getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(loginMember.getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(loginMember.getJobGroup().getLabel())
			);
	}

	@DisplayName("[질문글 아이디로 해당 질문글의 답변들을 조회할 수 있다.]")
	@Test
	void getAnswersByQuestionPostId() throws Exception {
		QuestionPost questionPost
			= questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		Member anotherMember = memberRepository.save(MemberFixture.member2());

		List<Answer> answers = answerRepository.saveAll(List.of(
			answerRepository.save(AnswerFixture.answer(questionPost.getId(), anotherMember)),
			answerRepository.save(AnswerFixture.answer(questionPost.getId(), anotherMember))
		));

		RegisterAnswerRequest request = new RegisterAnswerRequest("본문");
		mockMvc.perform(get("/api/question-posts/{questionPostId}/answers", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].answerId").value(answers.get(0).getId()))
			.andExpect(jsonPath("$.content[0].isQuestioner").value(false))
			.andExpect(jsonPath("$.content[1].answerId").value(answers.get(1).getId()))
			.andExpect(jsonPath("$.content[1].isQuestioner").value(false));
	}

	@DisplayName("[질문자는 답변을 채택할 수 있다.]")
	@Test
	void chooseAnswer() throws Exception {
		QuestionPost questionPost
			= questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		Member answerer = memberRepository.save(MemberFixture.member2());
		Answer answer = answerRepository.save(AnswerFixture.answer(questionPost.getId(), answerer));

		mockMvc.perform(post("/api/question-posts/answers/{answerId}", answer.getId())
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.content").value(answer.getContent()))
			.andExpect(jsonPath("$.isChosen").value(true))
			.andExpect(jsonPath("$.isQuestioner").value(false))
			.andExpect(jsonPath("$.memberInfo.memberId").value(answerer.getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(answerer.getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(answerer.getJobGroup().getLabel())
			);
	}
}