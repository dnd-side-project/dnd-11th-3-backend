package com.dnd.gongmuin.credit;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CreditDetail {

	DEPOSIT("입금"),
	WITHDRAWAL("출금");

	private final String label;

	public static CreditDetail of(String input) {
		return Arrays.stream(values())
			.filter(detail -> detail.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
