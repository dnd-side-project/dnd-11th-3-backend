package com.dnd.gongmuin.post_interaction.dto;

import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InteractionMapper {

	public static Interaction toInteraction(Long questionPostId, Long memberId, InteractionType type) {
		return Interaction.of(
			type,
			memberId,
			questionPostId
		);
	}

	public static InteractionCount toInteractionCount(Long questionPostId, InteractionType type) {
		return InteractionCount.of(
			type,
			questionPostId
		);
	}

	public static InteractionResponse toInteractionResponse(int count, InteractionType type) {
		return new InteractionResponse(
			count, type.getLabel()
		);
	}
}
