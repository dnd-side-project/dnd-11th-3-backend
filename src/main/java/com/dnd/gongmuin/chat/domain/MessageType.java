package com.dnd.gongmuin.chat.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

	IMAGE("이미지"),
	TEXT("텍스트"),
	VIDEO("비디오");

	private final String label;
}
