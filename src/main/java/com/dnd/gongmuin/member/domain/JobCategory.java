package com.dnd.gongmuin.member.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JobCategory {

	GAS("가스"); // TODO: 7/20/24 필드 추가

	private final String label;
}
