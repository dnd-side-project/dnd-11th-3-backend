package com.dnd.gongmuin.question_post.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[QuestionPostService 테스트]")
@ExtendWith(MockitoExtension.class)
class QuestionPostServiceTest {

	private final Member member = MemberFixture.member();

	@Mock
	private QuestionPostRepository questionPostRepository;

	@InjectMocks
	private QuestionPostService questionPostService;

	@DisplayName("[질문글을 등록할 수 있다.]")
	@Test
	void registerQuestionPost() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L);
		RegisterQuestionPostRequest request =
			RegisterQuestionPostRequest.of(
				"제목",
				"내용",
				List.of("image1.jpg", "image2.jpg"),
				1000,
				"공업"
			);

		given(questionPostRepository.save(any(QuestionPost.class)))
			.willReturn(questionPost);

		//when
		QuestionPostDetailResponse response = questionPostService.registerQuestionPost(request, member);

		//then
		assertAll(
			() -> assertThat(response.title()).isEqualTo(request.title()),
			() -> assertThat(response.content()).isEqualTo(request.content()),
			() -> assertThat(response.reward()).isEqualTo(request.reward()),
			() -> assertThat(response.targetJobGroup()).isEqualTo(request.targetJobGroup())
		);
	}

	@DisplayName("[질문글 아이디로 질문글을 상세 조회할 수 있다.]")
	@Test
	void getQuestionPostById() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L);
		given(questionPostRepository.findById(1L))
			.willReturn(Optional.of(questionPost));
		//when
		QuestionPostDetailResponse response = questionPostService.getQuestionPostById(questionPost.getId());

		//then
		assertThat(response.questionPostId()).isEqualTo(questionPost.getId());
	}

}
