package com.dnd.gongmuin.common.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InteractionCountFixture {

	public static InteractionCount interactionCount(
		InteractionType type,
		Long questionPostId
	) {
		InteractionCount interactionCount = InteractionCount.of(
			type,
			questionPostId
		);
		ReflectionTestUtils.setField(interactionCount, "id", 1L);
		return interactionCount;
	}
}
