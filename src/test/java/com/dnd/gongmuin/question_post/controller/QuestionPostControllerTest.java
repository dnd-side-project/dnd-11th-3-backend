package com.dnd.gongmuin.question_post.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.post_interaction.service.InteractionService;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.request.UpdateQuestionPostRequest;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[QuestionPost 통합 테스트]")
class QuestionPostControllerTest extends ApiTestSupport {

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private InteractionRepository interactionRepository;

	@Autowired
	private InteractionCountRepository interactionCountRepository;

	@Autowired
	private InteractionService interactionService;

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
		RegisterQuestionPostRequest request = new RegisterQuestionPostRequest(
			"제목",
			"정정기간에 여석이 있을까요?",
			List.of("image1.jpg", "image2.jpg"),
			2000,
			"공업"
		);

		mockMvc.perform(post("/api/question-posts")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(request.title()))
			.andExpect(jsonPath("$.content").value(request.content()))
			.andExpect(jsonPath("$.imageUrls[0]").value(request.imageUrls().get(0)))
			.andExpect(jsonPath("$.reward").value(request.reward()))
			.andExpect(jsonPath("$.targetJobGroup").value(request.targetJobGroup()))
			.andExpect(jsonPath("$.memberInfo.memberId").value(loginMember.getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(loginMember.getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(loginMember.getJobGroup().getLabel()));
	}

	@DisplayName("[보유 크레딧이 부족하면 질문글을 등록할 수 없다.]")
	@Test
	void registerQuestionPostFail() throws Exception {
		loginMember.decreaseCredit(5000);
		memberRepository.save(loginMember); // 크레딧

		RegisterQuestionPostRequest request = new RegisterQuestionPostRequest(
			"제목",
			"정정기간에 여석이 있을까요?",
			List.of("image1.jpg", "image2.jpg"),
			loginMember.getCredit() + 1,
			"공업"
		);

		mockMvc.perform(post("/api/question-posts")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code")
				.value(MemberErrorCode.NOT_ENOUGH_CREDIT.getCode()));
	}

	@DisplayName("[질문글을 아이디로 상세 조회할 수 있다.]")
	@Test
	void getQuestionPostById() throws Exception {
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));

		mockMvc.perform(get("/api/question-posts/{questionPostId}", questionPost.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(questionPost.getTitle()))
			.andExpect(jsonPath("$.content").value(questionPost.getContent()))
			.andExpect(jsonPath("$.imageUrls[0]").value(questionPost.getImages().get(0).getImageUrl()))
			.andExpect(jsonPath("$.reward").value(questionPost.getReward()))
			.andExpect(jsonPath("$.targetJobGroup").value(questionPost.getJobGroup().getLabel()))
			.andExpect(jsonPath("$.memberInfo.memberId").value(questionPost.getMember().getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(questionPost.getMember().getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(questionPost.getMember().getJobGroup().getLabel()))
			.andExpect(jsonPath("$.memberInfo.profileImageNo", is(greaterThanOrEqualTo(1))))
			.andExpect(jsonPath("$.memberInfo.profileImageNo", is(lessThanOrEqualTo(9))))
			.andExpect(jsonPath("$.isSaved").value(false))
			.andExpect(jsonPath("$.isRecommended").value(false))
			.andExpect(jsonPath("$.recommendCount").value(0))
			.andExpect(jsonPath("$.savedCount").value(0));
	}

	@DisplayName("[질문글을 저장 후 조회 시, 저장 여부가 true가 되고 저장 수가 1 증가한다.]")
	@Test
	void getQuestionPostById_afterSaved() throws Exception {
		Member questioner = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(questioner));
		interactionService.activateInteraction(questionPost.getId(), loginMember.getId(), InteractionType.SAVED);

		mockMvc.perform(get("/api/question-posts/{questionPostId}", questionPost.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSaved").value(true))
			.andExpect(jsonPath("$.isRecommended").value(false))
			.andExpect(jsonPath("$.savedCount").value(1))
			.andExpect(jsonPath("$.recommendCount").value(0));
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
				.cookie(accessToken))
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
				.cookie(accessToken))
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
				.cookie(accessToken))
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
				.cookie(accessToken))
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
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(3))
			.andExpect(jsonPath("$.content[0].questionPostId").value(questionPost3.getId()))
			.andExpect(jsonPath("$.content[1].questionPostId").value(questionPost1.getId()))
			.andExpect(jsonPath("$.content[2].questionPostId").value(questionPost2.getId()));
	}

	@DisplayName("[질문글 업데이트해 게시물 정보를 수정할 수 있다..]")
	@Test
	void updateQuestionPost() throws Exception {
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		UpdateQuestionPostRequest request = new UpdateQuestionPostRequest(
			questionPost.getTitle(),
			questionPost.getContent() + "ts",
			null,
			questionPost.getReward() + 1000,
			JobGroup.AD.getLabel()
		);
		mockMvc.perform(patch("/api/question-posts/{questionPostId}/edit", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(request.title()))
			.andExpect(jsonPath("$.content").value(request.content()))
			.andExpect(jsonPath("$.reward").value(request.reward()))
			.andExpect(jsonPath("$.targetJobGroup").value(request.targetJobGroup()))
			.andDo(MockMvcResultHandlers.print());
	}

	@DisplayName("[질문글 업데이트 시 이미지를 업데이트할 수 있다.]")
	@Test
	void updateQuestionPost_images() throws Exception {
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		List<String> updateImageUrls = List.of("img.url");
		UpdateQuestionPostRequest request = new UpdateQuestionPostRequest(
			questionPost.getTitle(),
			questionPost.getContent(),
			updateImageUrls,
			3000,
			questionPost.getJobGroup().getLabel()
		);
		mockMvc.perform(patch("/api/question-posts/{questionPostId}/edit", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.imageUrls[0]").value(updateImageUrls.get(0)))
			.andExpect(jsonPath("$.imageUrls.length()").value(updateImageUrls.size()))
			.andDo(MockMvcResultHandlers.print());
	}

	@DisplayName("[질문글 업데이트 시 이미지 필드가 null이면 변경 사항이 없다.]")
	@Test
	void updateQuestionPost_images_null() throws Exception {
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		UpdateQuestionPostRequest request = new UpdateQuestionPostRequest(
			questionPost.getTitle(),
			questionPost.getContent(),
			null,
			3000,
			questionPost.getJobGroup().getLabel()
		);
		mockMvc.perform(patch("/api/question-posts/{questionPostId}/edit", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.imageUrls[0]")
				.value(questionPost.getImages().get(0).getImageUrl()))
			.andExpect(jsonPath("$.imageUrls[1]")
				.value(questionPost.getImages().get(1).getImageUrl()))
			.andDo(MockMvcResultHandlers.print());
	}

	@DisplayName("[질문글 업데이트 시 이미지 필드가 빈 리스트이면, 기존 이미지를 모두 지운다]")
	@Test
	void updateQuestionPost_images_empty() throws Exception {
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		UpdateQuestionPostRequest request = new UpdateQuestionPostRequest(
			questionPost.getTitle(),
			questionPost.getContent(),
			Collections.emptyList(),
			3000,
			questionPost.getJobGroup().getLabel()
		);
		mockMvc.perform(patch("/api/question-posts/{questionPostId}/edit", questionPost.getId())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.imageUrls.length()")
				.value(0))
			.andDo(MockMvcResultHandlers.print());
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