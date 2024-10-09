package com.dnd.gongmuin.credit_history.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreditType {

	CHOOSE("채택하기", "출금"),
	CHOSEN("채택받기", "입금"),
	CHAT_REQUEST("채팅 요청", "출금"),
	CHAT_ACCEPT("채팅 수락", "입금"),
	CHAT_REFUND("채팅 환급", "입금");

	private final String label;
	private final String detail;

	public static CreditType of(String input) {
		return Arrays.stream(values())
			.filter(type -> type.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	public static CreditType fromDetail(String detail) {
		return Arrays.stream(values())
			.filter(type -> type.isDetailEqual(detail))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}

	private boolean isDetailEqual(String input) {
		return input.equals(this.detail);
	}
}
