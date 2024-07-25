package com.dnd.gongmuin.member.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobGroup {

	ENGINEERING("공업"); // TODO: 7/20/24 필드 추가

	private final String label;

	public static JobGroup of(String input) {
		return Arrays.stream(values())
			.filter(group -> group.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
