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

import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.RegisterQuestionPostResponse;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[QuestionPostService 테스트]")
@ExtendWith(MockitoExtension.class)
class QuestionPostServiceTest {

	private final Member member = MemberFixture.member();

	@Mock
	private QuestionPostRepository questionPostRepository;

	@Mock
	private InteractionCountRepository interactionCountRepository;

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
		RegisterQuestionPostResponse response = questionPostService.registerQuestionPost(request, member);

		//then
		assertAll(
			() -> assertThat(response.title()).isEqualTo(request.title()),
			() -> assertThat(response.content()).isEqualTo(request.content()),
			() -> assertThat(response.reward()).isEqualTo(request.reward()),
			() -> assertThat(response.targetJobGroup()).isEqualTo(request.targetJobGroup())
		);
	}

	@DisplayName("[질문글 아이디로 질문글을 상세 조회할 수 있다. 상호작용 이력 존재x]")
	@Test
	void getQuestionPostById_noInteraction() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.RECOMMEND
		)).willReturn(Optional.empty());

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.SAVED
		)).willReturn(Optional.empty());

		//when
		QuestionPostDetailResponse response
			= questionPostService.getQuestionPostById(questionPost.getId());

		//then
		assertAll(
			() -> assertThat(response.questionPostId()).isEqualTo(questionPost.getId()),
			() -> assertThat(response.recommendCount()).isZero(),
			() -> assertThat(response.savedCount()).isZero()
		);
	}

	@DisplayName("[질문글 아이디로 질문글을 상세 조회할 수 있다. 상호작용 이력 존재]")
	@Test
	void getQuestionPostById_interaction() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		InteractionCount recommendCount
			= InteractionCountFixture.interactionCount(InteractionType.RECOMMEND, questionPostId);
		InteractionCount savedCount
			= InteractionCountFixture.interactionCount(InteractionType.SAVED, questionPostId);

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.RECOMMEND
		)).willReturn(Optional.of(recommendCount));

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.SAVED
		)).willReturn(Optional.of(savedCount));

		//when
		QuestionPostDetailResponse response
			= questionPostService.getQuestionPostById(questionPost.getId());
		//then
		assertAll(
			() -> assertThat(response.questionPostId())
				.isEqualTo(questionPost.getId()),
			() -> assertThat(response.recommendCount())
				.isEqualTo(recommendCount.getCount()).isEqualTo(1),
			() -> assertThat(response.savedCount())
				.isEqualTo(savedCount.getCount()).isEqualTo(1)
		);
	}
}
