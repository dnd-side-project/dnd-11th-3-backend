package com.dnd.gongmuin.common.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.PostInteraction;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostInteractionFixture {

	public static PostInteraction postInteraction(
		InteractionType type,
		Long memberId,
		Long questionPostId
	){
		PostInteraction postInteraction = PostInteraction.of(
			type,
			memberId,
			questionPostId
		);
		ReflectionTestUtils.setField(postInteraction,"id",1L);
		return postInteraction;
	}
}
