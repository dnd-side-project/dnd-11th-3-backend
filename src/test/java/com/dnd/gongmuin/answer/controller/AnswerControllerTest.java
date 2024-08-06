package com.dnd.gongmuin.answer.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
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

	@AfterEach
	void teardown() {
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
		answerRepository.deleteAll();
	}

	@DisplayName("[타 유저가 답변을 등록할 수 있다.]")
	@Test
	void registerAnswerByOther() throws Exception {

		Member anotherMember = memberRepository.save(MemberFixture.member2());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(anotherMember));

		RegisterAnswerRequest request = RegisterAnswerRequest.from("본문");
		mockMvc.perform(post("/api/question-posts/{questionPostId}/answers", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken) //loginMember
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

		RegisterAnswerRequest request = RegisterAnswerRequest.from("본문");
		mockMvc.perform(post("/api/question-posts/{questionPostId}/answers", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken) //loginMember
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

}