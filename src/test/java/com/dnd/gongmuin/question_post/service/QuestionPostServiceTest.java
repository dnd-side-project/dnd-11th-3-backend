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
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.request.UpdateQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.RegisterQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.UpdateQuestionPostResponse;
import com.dnd.gongmuin.question_post.repository.QuestionPostImageRepository;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[QuestionPostService 테스트]")
@ExtendWith(MockitoExtension.class)
class QuestionPostServiceTest {

	private final Member member = MemberFixture.member(1L);

	@Mock
	private QuestionPostRepository questionPostRepository;

	@Mock
	private QuestionPostImageRepository questionPostImageRepository;

	@Mock
	private InteractionRepository interactionRepository;

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
			new RegisterQuestionPostRequest(
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

		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.SAVED))
			.willReturn(false);

		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.RECOMMEND))
			.willReturn(false);

		//when
		QuestionPostDetailResponse response
			= questionPostService.getQuestionPostById(questionPost.getId(), member);

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
			= questionPostService.getQuestionPostById(questionPost.getId(), member);
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

	@DisplayName("[질문글 아이디로 질문글을 상세 조회할 수 있다. 나의 추천 이력만 존재]")
	@Test
	void getQuestionPostById_my_interaction() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(questionPostId);
		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.SAVED))
			.willReturn(false);
		given(interactionRepository
			.existsByQuestionPostIdAndMemberIdAndTypeAndIsInteractedTrue(questionPostId, member.getId(),
				InteractionType.RECOMMEND))
			.willReturn(true);

		InteractionCount recommendCount
			= InteractionCountFixture.interactionCount(InteractionType.RECOMMEND, questionPostId);

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.SAVED
		)).willReturn(Optional.empty());

		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPostId,
			InteractionType.RECOMMEND
		)).willReturn(Optional.of(recommendCount));

		//when
		QuestionPostDetailResponse response
			= questionPostService.getQuestionPostById(questionPost.getId(), member);

		//then
		assertAll(
			() -> assertThat(response.questionPostId())
				.isEqualTo(questionPost.getId()),
			() -> assertThat(response.isSaved())
				.isFalse(),
			() -> assertThat(response.isRecommended())
				.isTrue(),
			() -> assertThat(response.questionPostId())
				.isEqualTo(questionPost.getId()),
			() -> assertThat(response.savedCount()).isZero(),
			() -> assertThat(response.recommendCount())
				.isEqualTo(recommendCount.getCount()).isEqualTo(1)
		);
	}

	@DisplayName("[질문글 업데이트를 할 수 있다.]")
	@Test
	void updateQuestionPost() {
		//given
		Long questionPostId = 1L;
		QuestionPost questionPost = QuestionPostFixture.questionPost(member);
		UpdateQuestionPostRequest request =
			new UpdateQuestionPostRequest(
				questionPost.getTitle() + "ㅇㅇㅇ",
				questionPost.getContent(),
				null,
				questionPost.getReward() * 2,
				"행정"
			);

		given(questionPostRepository.findById(questionPostId))
			.willReturn(Optional.of(questionPost));

		//when
		UpdateQuestionPostResponse response
			= questionPostService.updateQuestionPost(questionPostId, request);

		//then
		assertAll(
			() -> assertThat(response.title())
				.isEqualTo(request.title()),
			() -> assertThat(response.reward())
				.isEqualTo(request.reward()),
			() -> assertThat(response.targetJobGroup())
				.isEqualTo(request.targetJobGroup()),
			() -> assertThat(response.imageUrls())
				.isEqualTo(questionPost.getImages().stream()
					.map(QuestionPostImage::getImageUrl).toList())
		);
	}
}
