package com.dnd.gongmuin.post_interaction.domain;

import java.util.Arrays;

import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.post_interaction.exception.InteractionErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteractionType {

	SAVED("북마크"),
	RECOMMEND("추천");

	private final String label;

	public static InteractionType from(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(() -> new ValidationException(InteractionErrorCode.NOT_FOUND_TYPE));
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
