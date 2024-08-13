package com.dnd.gongmuin.post_interaction.dto;

import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteraction;
import com.dnd.gongmuin.post_interaction.domain.PostInteractionCount;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostInteractionMapper {

	public static PostInteraction toPostInteraction(Long questionPostId, Long memberId, InteractionType type) {
		return PostInteraction.of(
			type,
			memberId,
			questionPostId
		);
	}

	public static PostInteractionCount toPostInteractionCount(Long questionPostId, InteractionType type) {
		return PostInteractionCount.of(
			type,
			questionPostId
		);
	}

	public static PostInteractionResponse toPostInteractionResponse(int count, InteractionType type) {
		return new PostInteractionResponse(
			count, type.getLabel()
		);
	}
}
