package com.dnd.gongmuin.post_interaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteraction;
import com.dnd.gongmuin.post_interaction.domain.PostInteractionCount;
import com.dnd.gongmuin.post_interaction.dto.PostInteractionMapper;
import com.dnd.gongmuin.post_interaction.dto.PostInteractionResponse;
import com.dnd.gongmuin.post_interaction.exception.PostInteractionErrorCode;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.PostInteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.exception.QuestionPostErrorCode;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostInteractionService {

	private final PostInteractionRepository postInteractionRepository;
	private final PostInteractionCountRepository postInteractionCountRepository;
	private final QuestionPostRepository questionPostRepository;

	@Transactional
	public PostInteractionResponse activateInteraction(
		Long questionPostId,
		Long memberId,
		InteractionType type // 북마크, 추천
	) {
		int totalCount;
		if (!postInteractionRepository.existsByQuestionPostIdAndMemberIdAndType // 상호 작용 존재x -> 저장
			(questionPostId, memberId, type)
		) {
			totalCount = createInteraction(questionPostId, memberId, type);
		} else { // 존재 -> 값 업데이트
			totalCount = updateInteractionAndCount(questionPostId, memberId, type, true);
		}
		return PostInteractionMapper.toPostInteractionResponse(
			totalCount, type
		);
	}

	@Transactional
	public PostInteractionResponse inactivateInteraction(
		Long questionPostId,
		Long memberId,
		InteractionType type
	) {
		int totalCount = updateInteractionAndCount(questionPostId, memberId, type, false);
		return PostInteractionMapper.toPostInteractionResponse(
			totalCount, type
		);
	}

	private int createInteraction(
		Long questionPostId,
		Long memberId,
		InteractionType type
	) {
		validateIfPostExistsAndNotQuestioner(questionPostId, memberId);
		postInteractionRepository.save(
			PostInteractionMapper.toPostInteraction(questionPostId, memberId, type)
		);
		return postInteractionCountRepository // 게시글 상호작용이 없어도 타 회원에 인해 게시글 상호작용 수가 있을 수 있음
			.findByQuestionPostIdAndType(questionPostId, type)
			.orElseGet(
				() -> postInteractionCountRepository
					.save(PostInteractionMapper.toPostInteractionCount(questionPostId, type)) // 생성 시 count 1로 초기화
			)
			.getTotalCount();
	}

	private void validateIfPostExistsAndNotQuestioner(
		Long questionPostId,
		Long memberId
	) {
		QuestionPost questionPost = questionPostRepository.findById(questionPostId)
			.orElseThrow(() -> new NotFoundException(QuestionPostErrorCode.NOT_FOUND_QUESTION_POST));
		if (questionPost.isQuestioner(memberId)) {
			throw new ValidationException(PostInteractionErrorCode.ALREADY_UNINTERACTED);
		}
	}

	private int updateInteractionAndCount(
		Long questionPostId,
		Long memberId,
		InteractionType type,
		boolean isActivate
	) {
		int totalCount;
		PostInteraction postInteraction = getPostInteraction(questionPostId, memberId, type);
		PostInteractionCount postInteractionCount = getPostInteractionCount(questionPostId, type);

		if (isActivate) { //활성화
			postInteraction.updateIsInteractedTrue();
			totalCount = postInteractionCount.increaseTotalCount();
		} else { // 비활성화
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
