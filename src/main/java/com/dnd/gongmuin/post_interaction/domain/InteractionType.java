package com.dnd.gongmuin.post_interaction.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteractionType {

	SAVED("저장"),
	RECOMMEND("추천");

	private final String label;
}
