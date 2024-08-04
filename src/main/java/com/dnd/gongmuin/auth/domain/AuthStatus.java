package com.dnd.gongmuin.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthStatus {

	NEW("신규"),
	OLD("기존");

	private final String label;
}
