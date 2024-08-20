package com.dnd.gongmuin.member.domain;

import java.util.Arrays;

import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.exception.MemberErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobCategory {

	GAS("가스"); // TODO: 7/20/24 필드 추가

	private final String label;

	public static JobCategory from(String input) {
		return Arrays.stream(values())
			.filter(category -> category.isEqual(input))
			.findAny()
			.orElseThrow(() -> new ValidationException(MemberErrorCode.NOT_FOUND_JOB_CATEGORY));
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
