package com.dnd.gongmuin.credit;

import java.util.Arrays;

import com.dnd.gongmuin.member.domain.JobGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreditType {

	CHOOSE("채택하기", "출금"),
	CHOSEN("채택받기", "입금"),
	CHAT_REQUEST("채팅신청", "출금"),
	CHAT_ACCEPT("채팅받기", "입금");

	private final String label;
	private final String detail;

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
