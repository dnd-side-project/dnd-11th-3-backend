package com.dnd.gongmuin.post_interaction.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[Interaction 통합 테스트]")
class InteractionControllerTest extends ApiTestSupport {

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
				.cookie(accessToken)
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
		interactionRepository.save(InteractionFixture.interaction(InteractionType.RECOMMEND,
			loginMember.getId(), questionPost.getId()));
		interactionCountRepository.save(
			InteractionCountFixture.interactionCount(InteractionType.RECOMMEND, questionPost.getId())
		);

		mockMvc.perform(post("/api/question-posts/{questionPostId}/inactivated", questionPost.getId())
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
				.param("type", InteractionType.RECOMMEND.getLabel())
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
		interactionRepository.save(InteractionFixture.interaction(InteractionType.RECOMMEND,
			loginMember.getId(), questionPost.getId()));
		interactionCountRepository.save(
			InteractionCountFixture.interactionCount(InteractionType.RECOMMEND, questionPost.getId())
		);

		mockMvc.perform(post("/api/question-posts/{questionPostId}/inactivated", questionPost.getId())
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
				.param("type", "추천")
			)
			.andExpect(status().isOk());
	}
}