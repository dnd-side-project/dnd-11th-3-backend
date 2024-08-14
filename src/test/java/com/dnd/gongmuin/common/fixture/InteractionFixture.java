package com.dnd.gongmuin.common.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InteractionFixture {

	public static Interaction postInteraction(
		InteractionType type,
		Long memberId,
		Long questionPostId
	) {
		Interaction interaction = Interaction.of(
			type,
			memberId,
			questionPostId
		);
		ReflectionTestUtils.setField(interaction, "id", 1L);
		return interaction;
	}
}
