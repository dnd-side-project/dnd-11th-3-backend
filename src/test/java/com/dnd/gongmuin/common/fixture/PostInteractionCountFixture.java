package com.dnd.gongmuin.common.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteractionCount;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostInteractionCountFixture {

	public static PostInteractionCount postInteractionCount(
		InteractionType type,
		Long questionPostId
	) {
		PostInteractionCount postInteractionCount = PostInteractionCount.of(
			type,
			questionPostId
		);
		ReflectionTestUtils.setField(postInteractionCount, "id", 1L);
		return postInteractionCount;
	}
}
