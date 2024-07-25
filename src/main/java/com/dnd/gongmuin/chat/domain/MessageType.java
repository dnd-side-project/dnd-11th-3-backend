package com.dnd.gongmuin.chat.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

	IMAGE("이미지"),
	TEXT("텍스트"),
	VIDEO("비디오");

	private final String label;

	public static MessageType of(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
