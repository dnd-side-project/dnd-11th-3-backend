package com.dnd.gongmuin.post_interaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteraction;
import com.dnd.gongmuin.post_interaction.domain.PostInteractionCount;
import com.dnd.gongmuin.post_interaction.dto.PostInteractionMapper;
import com.dnd.gongmuin.post_interaction.dto.PostInteractionResponse;
import com.dnd.gongmuin.post_interaction.exception.PostInteractionErrorCode;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostInteractionService {

	private final PostInteractionRepository postInteractionRepository;
	private final PostInteractionCountRepository postInteractionCountRepository;

	@Transactional
	public PostInteractionResponse activateInteraction(Long questionPostId, Long memberId, InteractionType type) {
		int totalCount;
		if (!postInteractionRepository.existsByQuestionPostIdAndMemberIdAndType // 상호작용 존재x -> 저장
			(questionPostId, memberId, type)
		) {
			totalCount = createInteractionAndCount(questionPostId, memberId, type);
		} else { // 존재 -> 값 업데이트
			totalCount = updateInteractionAndCount(questionPostId, memberId, type, true);
		}
		return PostInteractionMapper.toPostInteractionResponse(
			totalCount, type
		);
	}

	@Transactional
	public PostInteractionResponse inactivateInteraction(Long questionPostId, Long memberId, InteractionType type) {
		int totalCount = updateInteractionAndCount(questionPostId, memberId, type, false);
		return PostInteractionMapper.toPostInteractionResponse(
			totalCount, type
		);
	}

	private int createInteractionAndCount(Long questionPostId, Long memberId, InteractionType type) {
		postInteractionRepository.save(PostInteractionMapper.toPostInteraction(questionPostId, memberId, type));
		return postInteractionCountRepository
			.save(PostInteractionMapper.toPostInteractionCount(questionPostId, type))
			.getTotalCount();
	}

	private int updateInteractionAndCount(Long questionPostId, Long memberId, InteractionType type, boolean isActivate) {
		int totalCount;
		PostInteraction postInteraction = getPostInteraction(questionPostId, memberId, type);
		PostInteractionCount postInteractionCount = getPostInteractionCount(questionPostId, type);

		if (isActivate){
			postInteraction.updateIsInteractedTrue();
			totalCount = postInteractionCount.increaseTotalCount();
		} else {
			postInteraction.updateIsInteractedFalse();
			totalCount = postInteractionCount.decreaseTotalCount();
		}
		return totalCount;
	}

	private PostInteraction getPostInteraction(Long questionPostId, Long memberId, InteractionType type) {
		return postInteractionRepository.findByQuestionPostIdAndMemberIdAndType(
			questionPostId, memberId, type
		).orElseThrow(() -> new NotFoundException(PostInteractionErrorCode.NOT_FOUND_POST_INTERACTION));
	}

	private PostInteractionCount getPostInteractionCount(Long questionPostId, InteractionType type) {
		return postInteractionCountRepository
			.findByQuestionPostIdAndType(questionPostId, type)
			.orElseThrow();
	}

}
