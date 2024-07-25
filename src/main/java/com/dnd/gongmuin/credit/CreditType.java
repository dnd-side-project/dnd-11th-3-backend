package com.dnd.gongmuin.credit;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreditType {

	CHOOSE("채택하기"),
	CHOSEN("채택받기"),
	CHAT_REQUEST("채팅신청"),
	CHAT_ACCEPT("채팅받기");

	private final String label;

	public static CreditType of(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
