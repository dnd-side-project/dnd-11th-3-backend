package com.dnd.gongmuin.question_post.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[QuestionPost 통합 테스트]")
class QuestionPostControllerTest extends ApiTestSupport {

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@AfterEach
	void teardown() {
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
	}

	@DisplayName("[질문글을 등록할 수 있다.]")
	@Test
	void registerQuestionPost() throws Exception {
		RegisterQuestionPostRequest request = RegisterQuestionPostRequest.of(
			"제목",
			"내용",
			List.of("image1.jpg", "image2.jpg"),
			1000,
			"공업"
		);

		mockMvc.perform(post("/api/question-posts")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(request.title()))
			.andExpect(jsonPath("$.content").value(request.content()))
			.andExpect(jsonPath("$.imageUrls[0]").value(request.imageUrls().get(0)))
			.andExpect(jsonPath("$.reward").value(request.reward()))
			.andExpect(jsonPath("$.targetJobGroup").value(request.targetJobGroup()))
			.andExpect(jsonPath("$.memberInfo.memberId").value(loginMember.getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(loginMember.getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(loginMember.getJobGroup().getLabel())
			);
	}

	@DisplayName("[보유 크레딧이 부족하면 질문글을 등록할 수 없다.]")
	@Test
	void registerQuestionPostFail() throws Exception {
		RegisterQuestionPostRequest request = RegisterQuestionPostRequest.of(
			"제목",
			"내용",
			List.of("image1.jpg", "image2.jpg"),
			loginMember.getCredit() + 1,
			"공업"
		);

		mockMvc.perform(post("/api/question-posts")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code")
				.value(QuestionPostErrorCode.NOT_ENOUGH_CREDIT.getCode()));
	}

	@DisplayName("[질문글을 조회할 수 있다.]")
	@Test
	void getQuestionPostById() throws Exception {
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));

		mockMvc.perform(get("/api/question-posts/{questionPostId}", questionPost.getId())
				.header(AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(questionPost.getTitle()))
			.andExpect(jsonPath("$.content").value(questionPost.getContent()))
			.andExpect(jsonPath("$.imageUrls[0]").value(questionPost.getImages().get(0).getImageUrl()))
			.andExpect(jsonPath("$.reward").value(questionPost.getReward()))
			.andExpect(jsonPath("$.targetJobGroup").value(questionPost.getJobGroup().getLabel()))
			.andExpect(jsonPath("$.memberInfo.memberId").value(questionPost.getMember().getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(questionPost.getMember().getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(questionPost.getMember().getJobGroup().getLabel())
			);
	}
}
