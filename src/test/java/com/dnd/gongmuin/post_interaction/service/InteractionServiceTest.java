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
import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
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

	@Mock
	private InteractionRepository interactionRepository;

	@Mock
	private InteractionCountRepository interactionCountRepository;

	@Mock
	private QuestionPostRepository questionPostRepository;

	@InjectMocks
	private InteractionService interactionService;

	@DisplayName("[상호작용을 새로 활성화한다. 기존에 게시글 상호작용 수가 저장되어 있다.]")
	@Test
	void activateInteraction_create1() {
		//given
		InteractionType type = InteractionType.RECOMMEND;
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		Interaction interaction = Interaction.of(type, interactor.getId(), questionPost.getId());
		InteractionCount interactionCount = InteractionCount.of(type, interactor.getId());

		given(interactionRepository.existsByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(), interactor.getId(), type
		)).willReturn(false); // 생성
		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(interactionRepository.save(any(Interaction.class)))
			.willReturn(interaction);
		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPost.getId(),type)).willReturn(Optional.of(interactionCount));

		//when
		InteractionResponse response = interactionService.activateInteraction(1L, 2L,
			type);

		//then
		assertAll(
			() -> assertThat(response.count()).isEqualTo(1),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}

	@DisplayName("[상호작용을 새로 활성화한다. 기존에 게시글 상호작용 수가 저장되어있지 않다.]")
	@Test
	void activateInteraction_create2() {
		//given
		InteractionType type = InteractionType.RECOMMEND;
		QuestionPost questionPost = QuestionPostFixture.questionPost(1L, questioner);
		Interaction interaction = Interaction.of(type, interactor.getId(), questionPost.getId());
		InteractionCount interactionCount = InteractionCount.of(type, interactor.getId());

		given(interactionRepository.existsByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(), interactor.getId(), type
		)).willReturn(false); // 생성
		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(interactionRepository.save(any(Interaction.class)))
			.willReturn(interaction);
		given(interactionCountRepository.findByQuestionPostIdAndType(
			questionPost.getId(),type)).willReturn(Optional.empty());
		given(interactionCountRepository.save(any(InteractionCount.class)))
			.willReturn(interactionCount);

		//when
		InteractionResponse response
			= interactionService.activateInteraction(1L, 2L, type);

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
		Interaction interaction = InteractionFixture.postInteraction(type, interactor.getId(),
			questionPost.getId());
		InteractionCount interactionCount = InteractionCountFixture.postInteractionCount(type,
			interactor.getId());
		interaction.updateIsInteractedFalse();
		interactionCount.decreaseTotalCount();

		given(interactionRepository.existsByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(), interactor.getId(), type
		)).willReturn(true); // 업데이트
		given(interactionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(),
			interactor.getId(),
			type
		)).willReturn(Optional.of(interaction));
		given(interactionCountRepository.findByQuestionPostIdAndType(
			interactionCount.getId(), type))
			.willReturn(Optional.of(interactionCount));

		//when
		InteractionResponse response = interactionService.activateInteraction(1L, 2L,
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
		Interaction interaction = InteractionFixture.postInteraction(type, interactor.getId(),
			questionPost.getId());
		InteractionCount interactionCount = InteractionCountFixture.postInteractionCount(type,
			interactor.getId());

		given(interactionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPost.getId(),
			interactor.getId(),
			type
		)).willReturn(Optional.of(interaction));
		given(interactionCountRepository.findByQuestionPostIdAndType(
			interactionCount.getId(), type))
			.willReturn(Optional.of(interactionCount));

		//when
		InteractionResponse response = interactionService.inactivateInteraction(1L, 2L,
			type);

		//then
		assertAll(
			() -> assertThat(response.count()).isZero(),
			() -> assertThat(response.interactionType()).isEqualTo(type.getLabel())
		);
	}
}