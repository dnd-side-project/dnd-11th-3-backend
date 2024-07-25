package com.dnd.gongmuin.post_interaction.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteractionType {

	SAVED("저장"),
	RECOMMEND("추천");

	private final String label;

	public static InteractionType of(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
