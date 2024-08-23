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

	ENG("공업"),
	ME("기계"),
	CSM("법원경비관리"),
	PH("보건"),
	PHH("보건위생"),
	JA("사법행정사무"),
	ICT("정보통신"),
	AD("행정");

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
