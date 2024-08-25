package com.dnd.gongmuin.post_interaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.dto.InteractionMapper;
import com.dnd.gongmuin.post_interaction.dto.InteractionResponse;
import com.dnd.gongmuin.post_interaction.exception.InteractionErrorCode;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InteractionService {

	private final InteractionRepository interactionRepository;
	private final InteractionCountRepository interactionCountRepository;
	private final QuestionPostRepository questionPostRepository;

	@Transactional
	public InteractionResponse activateInteraction(
		Long questionPostId,
		Long memberId,
		InteractionType type // 북마크, 추천
	) {
		validateIfPostExistsAndNotQuestioner(questionPostId, memberId);

		interactionRepository
			.findByQuestionPostIdAndMemberIdAndType(questionPostId, memberId, type)
			.ifPresentOrElse(
				interaction -> interaction.updateIsInteracted(true),
				() -> interactionRepository.save(
					InteractionMapper.toInteraction(questionPostId, memberId, type)
				)
			);

		int count = interactionCountRepository
			.findByQuestionPostIdAndType(questionPostId, type)
			.map(InteractionCount::increaseCount)
			.orElseGet(() -> {
				interactionCountRepository.save(
					InteractionMapper.toInteractionCount(questionPostId, type)
				);
				return 1;
			});
		return InteractionMapper.toInteractionResponse(count, type);
	}

	@Transactional
	public InteractionResponse inactivateInteraction(
		Long questionPostId,
		Long memberId,
		InteractionType type // 북마크, 추천
	) {
		getPostInteraction(questionPostId, memberId, type)
			.updateIsInteracted(false);
		int count = getPostInteractionCount(questionPostId, type)
			.decreaseCount();
		return InteractionMapper.toInteractionResponse(count, type);
	}

	private void validateIfPostExistsAndNotQuestioner(
		Long questionPostId,
		Long memberId
	) {
		QuestionPost questionPost = questionPostRepository.findById(questionPostId)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
		if (questionPost.isQuestioner(memberId)) {
			throw new ValidationException(InteractionErrorCode.INTERACTION_NOT_ALLOWED);
		}
	}

	private Interaction getPostInteraction(Long questionPostId, Long memberId, InteractionType type) {
		return interactionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPostId, memberId, type
		).orElseThrow(() -> new NotFoundException(InteractionErrorCode.NOT_FOUND_INTERACTION));
	}

	private InteractionCount getPostInteractionCount(Long questionPostId, InteractionType type) {
		return interactionCountRepository
			.findByQuestionPostIdAndType(questionPostId, type)
			.orElseThrow(() -> new NotFoundException(InteractionErrorCode.NOT_FOUND_INTERACTION_COUNT));
	}
}
