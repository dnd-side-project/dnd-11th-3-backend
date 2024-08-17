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
		int count;
		if (!interactionRepository.existsByQuestionPostIdAndMemberIdAndType // 상호 작용 존재x -> 저장
			(questionPostId, memberId, type)
		) {
			count = createInteraction(questionPostId, memberId, type);
		} else { // 존재 -> 값 업데이트
			count = updateInteractionAndCount(questionPostId, memberId, type, true);
		}
		return InteractionMapper.toPostInteractionResponse(
			count, type
		);
	}

	@Transactional
	public InteractionResponse inactivateInteraction(
		Long questionPostId,
		Long memberId,
		InteractionType type
	) {
		int count = updateInteractionAndCount(questionPostId, memberId, type, false);
		return InteractionMapper.toPostInteractionResponse(
			count, type
		);
	}

	private int createInteraction(
		Long questionPostId,
		Long memberId,
		InteractionType type
	) {
		validateIfPostExistsAndNotQuestioner(questionPostId, memberId);
		interactionRepository.save(
			InteractionMapper.toPostInteraction(questionPostId, memberId, type)
		);
		return interactionCountRepository // 게시글 상호작용이 없어도 타 회원에 인해 게시글 상호작용 수가 있을 수 있음
			.findByQuestionPostIdAndType(questionPostId, type)
			.orElseGet(
				() -> interactionCountRepository
					.save(InteractionMapper.toPostInteractionCount(questionPostId, type)) // 생성 시 count 1로 초기화
			)
			.getCount();
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

	private int updateInteractionAndCount(
		Long questionPostId,
		Long memberId,
		InteractionType type,
		boolean isActivate
	) {
		int count;
		Interaction interaction = getPostInteraction(questionPostId, memberId, type);
		InteractionCount interactionCount = getPostInteractionCount(questionPostId, type);

		if (isActivate) { //활성화
			interaction.updateIsInteracted(true);
			count = interactionCount.increaseCount();
		} else { // 비활성화
			interaction.updateIsInteracted(false);
			count = interactionCount.decreaseCount();
		}
		return count;
	}

	private Interaction getPostInteraction(Long questionPostId, Long memberId, InteractionType type) {
		return interactionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPostId, memberId, type
		).orElseThrow(() -> new NotFoundException(InteractionErrorCode.NOT_FOUND_POST_INTERACTION));
	}

	private InteractionCount getPostInteractionCount(Long questionPostId, InteractionType type) {
		return interactionCountRepository
			.findByQuestionPostIdAndType(questionPostId, type)
			.orElseThrow();
	}
}
