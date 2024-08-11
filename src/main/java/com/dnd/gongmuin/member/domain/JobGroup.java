package com.dnd.gongmuin.member.domain;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobGroup {

	ENGINEERING("공업"),
	ADMINISTRATION("행정"),
	MACHINE("기계");

	private final String label;

	public static JobGroup of(String input) {
		return Arrays.stream(values())
			.filter(group -> group.isEqual(input))
			.findAny()
			.orElseThrow(IllegalArgumentException::new);
	}

	public static List<JobGroup> of(List<String> labels){
		return labels.stream()
			.map(JobGroup::of)
			.toList();
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
