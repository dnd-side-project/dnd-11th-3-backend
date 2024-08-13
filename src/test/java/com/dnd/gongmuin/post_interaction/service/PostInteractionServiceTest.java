package com.dnd.gongmuin.post_interaction.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.PostInteractionCountFixture;
import com.dnd.gongmuin.common.fixture.PostInteractionFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteraction;
import com.dnd.gongmuin.post_interaction.domain.PostInteractionCount;
import com.dnd.gongmuin.post_interaction.dto.PostInteractionResponse;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[PostInteractionService 테스트]")
@ExtendWith(MockitoExtension.class)
class PostInteractionServiceTest {

	private final Member questioner = MemberFixture.member(1L);
	private final Member interactor = MemberFixture.member(2L);

	@Mock
	private PostInteractionRepository postInteractionRepository;

	@Mock
	private PostInteractionCountRepository postInteractionCountRepository;

	@Mock
	private QuestionPostRepository questionPostRepository;

	@InjectMocks
	private PostInteractionService postInteractionService;

	@DisplayName("[상호작용을 새로 활성화한다.]")
	@Test
	void activateInteraction_create() {
		//given
		InteractionType type = InteractionType.RECOMMEND;
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		PostInteraction postInteraction = PostInteraction.of(type, interactor.getId(), questionPost.getId());
		PostInteractionCount postInteractionCount = PostInteractionCount.of(type, interactor.getId());

		given(postInteractionRepository.existsByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(), interactor.getId(), type
		)).willReturn(false); // 생성
		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(postInteractionRepository.save(any(PostInteraction.class)))
			.willReturn(postInteraction);
		given(postInteractionCountRepository.save(any(PostInteractionCount.class)))
			.willReturn(postInteractionCount);

		//when
		PostInteractionResponse response = postInteractionService.activateInteraction(1L, 2L,
			type);

		//then
		assertAll(
			() -> assertThat(response.count()).isEqualTo(1),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}

	@DisplayName("[비활성화된 상호작용을 재활성화한다.]")
	@Test
	void activateInteraction_update() {
		//given
		InteractionType type = InteractionType.RECOMMEND;
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		PostInteraction postInteraction = PostInteractionFixture.postInteraction(type, interactor.getId(),
			questionPost.getId());
		PostInteractionCount postInteractionCount = PostInteractionCountFixture.postInteractionCount(type,
			interactor.getId());
		postInteraction.updateIsInteractedFalse();
		postInteractionCount.decreaseTotalCount();

		given(postInteractionRepository.existsByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(), interactor.getId(), type
		)).willReturn(true); // 업데이트
		given(postInteractionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(),
			interactor.getId(),
			type
		)).willReturn(Optional.of(postInteraction));
		given(postInteractionCountRepository.findByQuestionPostIdAndType(
			postInteractionCount.getId(), type))
			.willReturn(Optional.of(postInteractionCount));

		//when
		PostInteractionResponse response = postInteractionService.activateInteraction(1L, 2L,
			type);

		//then
		assertAll(
			() -> assertThat(response.count()).isEqualTo(1),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}

	@DisplayName("[활성화된 상호작용을 비활성화한다.]")
	@Test
	void inactivateInteraction() {
		//given
		InteractionType type = InteractionType.RECOMMEND;
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		PostInteraction postInteraction = PostInteractionFixture.postInteraction(type, interactor.getId(),
			questionPost.getId());
		PostInteractionCount postInteractionCount = PostInteractionCountFixture.postInteractionCount(type,
			interactor.getId());

		given(postInteractionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(),
			interactor.getId(),
			type
		)).willReturn(Optional.of(postInteraction));
		given(postInteractionCountRepository.findByQuestionPostIdAndType(
			postInteractionCount.getId(), type))
			.willReturn(Optional.of(postInteractionCount));

		//when
		PostInteractionResponse response = postInteractionService.inactivateInteraction(1L, 2L,
			type);

		//then
		assertAll(
			() -> assertThat(response.count()).isZero(),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}
}