package com.dnd.gongmuin.question_post.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.dto.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@Disabled
@DisplayName("[QuestionPost 통합 테스트]")
public class QuestionPostControllerTest extends ApiTestSupport {

	private Member member;

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void setUp() {
		member = memberRepository.save(MemberFixture.member());
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

		// TODO: 시큐리티 구현 후 member객체 받아오기
		mockMvc.perform(post("/api/question-posts")
				.content(toJson(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.title").value(request.title()))
			.andExpect(jsonPath("$.content").value(request.content()))
			.andExpect(jsonPath("$.imageUrls").value(request.imageUrls()))
			.andExpect(jsonPath("$.reward").value(request.reward()))
			.andExpect(jsonPath("$.targetJobGroup").value(request.targetJobGroup()))
			.andExpect(jsonPath("$.memberInfo.memberId").value(member.getId()))
			.andExpect(jsonPath("$.memberInfo.nickname").value(member.getNickname()))
			.andExpect(jsonPath("$.memberInfo.memberJobGroup").value(member.getJobGroup())
		);

	}
}
