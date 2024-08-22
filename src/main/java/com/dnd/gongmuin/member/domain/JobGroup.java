package com.dnd.gongmuin.member.domain;

import java.util.Arrays;
import java.util.List;

import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.exception.MemberErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobGroup {

	ENGINEERING("공업"),
	ADMINISTRATION("행정"),
	MACHINE("기계");

	private final String label;

	public static JobGroup from(String input) {
		return Arrays.stream(values())
			.filter(group -> group.isEqual(input))
			.findAny()
			.orElseThrow(() -> new ValidationException(MemberErrorCode.NOT_FOUND_JOB_GROUP));
	}

	public static List<JobGroup> from(List<String> labels) {
		return labels.stream()
			.map(JobGroup::from)
			.toList();
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
