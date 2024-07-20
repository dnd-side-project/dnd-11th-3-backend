package com.dnd.gongmuin.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobGroup {

	ENGINEERING("공업"); // TODO: 7/20/24 필드 추가

	private final String label;
}
