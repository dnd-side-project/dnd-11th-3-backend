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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[QuestionPost 통합 테스트]")
class QuestionPostControllerTest extends ApiTestSupport {

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private InteractionRepository interactionRepository;

	@Autowired
	private InteractionCountRepository interactionCountRepository;

	@AfterEach
	void teardown() {
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
		interactionRepository.deleteAll();
		interactionCountRepository.deleteAll();
	}

	@DisplayName("[질문글을 등록할 수 있다.]")
	@Test
	void registerQuestionPost() throws Exception {
		RegisterQuestionPostRequest request = RegisterQuestionPostRequest.of(
			"제목",
			"정정기간에 여석이 있을까요?",
			List.of("image1.jpg", "image2.jpg"),
			2000,
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
		loginMember.decreaseCredit(5000);
		memberRepository.save(loginMember); // 크레딧

		RegisterQuestionPostRequest request = RegisterQuestionPostRequest.of(
			"제목",
			"정정기간에 여석이 있을까요?",
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
				.value(MemberErrorCode.NOT_ENOUGH_CREDIT.getCode()));
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
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(questionPost.getMember().getJobGroup().getLabel()))
			.andExpect(jsonPath("$.recommendCount").value(0))
			.andExpect(jsonPath("$.savedCount").value(0)
			);
	}

	@DisplayName("[질문글을 키워드로 검색할 수 있다.]")
	@Test
	void searchQuestionPost() throws Exception {
		QuestionPost questionPost1 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember, "발령"));
		QuestionPost questionPost2 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember, "발령대기"));
		QuestionPost questionPost3 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember, "반품"));
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		mockMvc.perform(get("/api/question-posts/search")
				.param("keyword", "발령")
				.header(AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost2.getId())) //최신순 정렬
			.andExpect(jsonPath("$.content[1].questionPostId").value(questionPost1.getId()));
	}

	@DisplayName("[질문글을 여러 직군들로 필터링할 수 있다.]")
	@Test
	void searchQuestionPostByCategories() throws Exception {
		QuestionPost questionPost1 = questionPostRepository.save(QuestionPostFixture.questionPost("기계", loginMember));
		QuestionPost questionPost2 = questionPostRepository.save(QuestionPostFixture.questionPost("기계", loginMember));
		QuestionPost questionPost3 = questionPostRepository.save(QuestionPostFixture.questionPost("공업", loginMember));
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		mockMvc.perform(get("/api/question-posts/search")
				.param("jobGroups", "공업", "행정")
				.header(AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(1))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost3.getId()));
	}

	@DisplayName("[질문글을 필터링 직군이 3개 넘어가면 예외가 발생한다.]")
	@Test
	void searchQuestionPostByCategoriesFails() throws Exception {
		QuestionPost questionPost1 = questionPostRepository.save(QuestionPostFixture.questionPost("기계", loginMember));
		QuestionPost questionPost2 = questionPostRepository.save(QuestionPostFixture.questionPost("기계", loginMember));
		QuestionPost questionPost3 = questionPostRepository.save(QuestionPostFixture.questionPost("공업", loginMember));
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		mockMvc.perform(get("/api/question-posts/search")
				.param("jobGroups", "공업", "행정", "기계", "우정")
				.header(AUTHORIZATION, accessToken))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message")
				.value("직군은 3개까지 선택 가능합니다."));
	}

	@DisplayName("[질문글을 채택여부로 필터링할 수 있다.]")
	@Test
	void searchQuestionPostByIsChosen() throws Exception {
		QuestionPost questionPost1 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		QuestionPost questionPost2 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		ReflectionTestUtils.setField(questionPost2, "isChosen", true);
		QuestionPost questionPost3 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		mockMvc.perform(get("/api/question-posts/search")
				.param("isChosen", "true")
				.header(AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(1))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost2.getId()));
	}

	@DisplayName("[직군에 맞는 추천 질문 게시물을 조회할 수 있다. 추천순>북마크순]")
	@Test
	void getRecommendQuestionPosts() throws Exception {
		QuestionPost questionPost1 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		QuestionPost questionPost2 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		QuestionPost questionPost3 = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		interactPost(questionPost3.getId(), InteractionType.RECOMMEND);
		interactPost(questionPost1.getId(), InteractionType.SAVED);
		mockMvc.perform(get("/api/question-posts/recommends")
				.header(AUTHORIZATION, accessToken))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(3))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost3.getId()))
			.andExpect(jsonPath("$.content[1].questionPostId").value(questionPost1.getId()))
			.andExpect(jsonPath("$.content[2].questionPostId").value(questionPost2.getId()));
	}

	private void interactPost(Long questionPostId, InteractionType type) {
		Interaction interaction =
			InteractionFixture.interaction(type, 2L, questionPostId);
		interactionRepository.save(interaction);
		InteractionCount interactionCount =
			InteractionCountFixture.interactionCount(type, questionPostId);
		interactionCountRepository.save(interactionCount);
	}
}