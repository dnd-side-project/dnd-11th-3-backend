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

import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.dto.InteractionResponse;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[InteractionService 테스트]")
@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {

	private final Member questioner = MemberFixture.member(1L);
	private final Member interactor = MemberFixture.member(2L);
	private final InteractionType type = InteractionType.RECOMMEND;

	@Mock
	private InteractionRepository interactionRepository;

	@Mock
	private InteractionCountRepository interactionCountRepository;

	@Mock
	private QuestionPostRepository questionPostRepository;

	@InjectMocks
	private InteractionService interactionService;

	@DisplayName("[게시글에 최초로 추천을 한다.]")
	@Test
	void activateInteraction_create1() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		Interaction interaction = Interaction.of(type, interactor.getId(), questionPost.getId());
		InteractionCount interactionCount = InteractionCount.of(type, interactor.getId());

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(interactionRepository.save(any(Interaction.class)))
			.willReturn(interaction);
		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPost.getId(), type)
		).willReturn(Optional.empty());
		given(interactionCountRepository.save(any(InteractionCount.class)))
			.willReturn(interactionCount);

		//when
		InteractionResponse response
			= interactionService.activateInteraction(1L, interactor.getId(), type);

		//then
		assertAll(
			() -> assertThat(response.count()).isEqualTo(1),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}

	@DisplayName("[다른 사람이 추천했던 게시글에 대해 추천한다.]")
	@Test
	void activateInteraction_create2() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		Interaction interaction = Interaction.of(type, interactor.getId(), questionPost.getId());
		InteractionCount interactionCount = InteractionCount.of(type, interactor.getId());

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(interactionRepository.save(any(Interaction.class)))
			.willReturn(interaction);
		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPost.getId(), type)
		).willReturn(Optional.of(interactionCount));

		//when
		InteractionResponse response = interactionService
			.activateInteraction(1L, interactor.getId(), type);

		//then
		assertAll(
			() -> assertThat(response.count()).isEqualTo(2),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}

	@DisplayName("[기존에 추천 취소했던 게시글을 재추천한다.]")
	@Test
	void activateInteraction_update() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		Interaction interaction = InteractionFixture.interaction(1L, type, interactor.getId(),
			questionPost.getId());
		InteractionCount interactionCount = InteractionCountFixture.interactionCount(1L, type,
			interactor.getId());
		interaction.updateIsInteracted(false);
		interactionCount.decreaseCount();

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));

		given(interactionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(),
			interactor.getId(),
			type
		)).willReturn(Optional.of(interaction));

		given(interactionCountRepository.findByQuestionPostIdAndType(
			interactionCount.getId(), type))
			.willReturn(Optional.of(interactionCount));

		//when
		InteractionResponse response =
			interactionService.activateInteraction(1L, interactor.getId(), type);

		//then
		assertAll(
			() -> assertThat(response.count()).isEqualTo(1),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}

	@DisplayName("[게시글 추천을 취소한다.]")
	@Test
	void inactivateInteraction() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		Interaction interaction = InteractionFixture.interaction(
			1L, type, interactor.getId(), questionPost.getId()
		);
		InteractionCount interactionCount =
			InteractionCountFixture.interactionCount(1L, type, interactor.getId());

		given(interactionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(),
			interactor.getId(),
			type
		)).willReturn(Optional.of(interaction));

		given(interactionCountRepository.findByQuestionPostIdAndType(
			interactionCount.getId(), type)
		).willReturn(Optional.of(interactionCount));

		//when
		InteractionResponse response = interactionService
			.inactivateInteraction(1L, interactor.getId(), type);

		//then
		assertAll(
			() -> assertThat(response.count()).isZero(),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}
}