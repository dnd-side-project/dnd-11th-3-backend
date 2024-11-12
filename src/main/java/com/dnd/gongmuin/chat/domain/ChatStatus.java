package com.dnd.gongmuin.chat.domain;

import java.util.Arrays;
import java.util.List;

import com.dnd.gongmuin.chat.exception.ChatErrorCode;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatStatus {

	PENDING("요청중"),
	ACCEPTED("수락됨"),
	REJECTED("거절됨");

	private final String label;

	public static ChatStatus from(String input) {
		return Arrays.stream(values())
			.filter(status -> status.isEqual(input))
			.findAny()
			.orElseThrow(() -> new ValidationException(ChatErrorCode.NOT_FOUND_CHAT_STATUS));
	}

	public static List<ChatStatus> from(List<String> inputs) {
		return inputs.stream()
			.map(input -> Arrays.stream(ChatStatus.values())
				.filter(status -> status.isEqual(input))
				.findAny()
				.orElseThrow(() -> new ValidationException(ChatErrorCode.NOT_FOUND_CHAT_STATUS)))
			.toList();
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
