package com.dnd.gongmuin.post_interaction.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.PostInteractionCountFixture;
import com.dnd.gongmuin.common.fixture.PostInteractionFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[PostInteraction 통합 테스트]")
class PostInteractionControllerTest extends ApiTestSupport {

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private PostInteractionRepository postInteractionRepository;

	@Autowired
	private PostInteractionCountRepository postInteractionCountRepository;

	@AfterEach
	void teardown() {
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
	}

	@DisplayName("[상호작용을 새로 활성화할 수 있다.]")
	@Test
	void activateInteraction_new() throws Exception {
		Member questioner = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(
			QuestionPostFixture.questionPost(questioner)
		);

		mockMvc.perform(post("/api/question-posts/{questionPostId}/activated", questionPost.getId())
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
				.param("type", "추천")
			)
			.andExpect(status().isOk());
	}

	@DisplayName("[기존 비활성화된 상호작용을 활성화할 수 있다.]")
	@Test
	void activateInteraction_old() throws Exception {
		Member questioner = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(
			QuestionPostFixture.questionPost(questioner)
		);
		postInteractionRepository.save(PostInteractionFixture.postInteraction(InteractionType.RECOMMEND,
			loginMember.getId(), questionPost.getId()));
		postInteractionCountRepository.save(
			PostInteractionCountFixture.postInteractionCount(InteractionType.RECOMMEND, questionPost.getId())
		);

		mockMvc.perform(post("/api/question-posts/{questionPostId}/inactivated", questionPost.getId())
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
				.param("type", "추천")
			)
			.andExpect(status().isOk());
	}

	@DisplayName("[상호작용을 비활성화할 수 있다.]")
	@Test
	void inactivateInteraction() throws Exception {
		Member questioner = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(
			QuestionPostFixture.questionPost(questioner)
		);
		postInteractionRepository.save(PostInteractionFixture.postInteraction(InteractionType.RECOMMEND,
			loginMember.getId(), questionPost.getId()));
		postInteractionCountRepository.save(
			PostInteractionCountFixture.postInteractionCount(InteractionType.RECOMMEND, questionPost.getId())
		);

		mockMvc.perform(post("/api/question-posts/{questionPostId}/inactivated", questionPost.getId())
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
				.param("type", "추천")
			)
			.andExpect(status().isOk());
	}
}