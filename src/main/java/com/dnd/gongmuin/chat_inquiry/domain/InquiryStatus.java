package com.dnd.gongmuin.chat_inquiry.domain;

import java.util.Arrays;

import com.dnd.gongmuin.chat_inquiry.exception.ChatInquiryErrorCode;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {

	PENDING("요청중"),
	ACCEPTED("수락됨"),
	REJECTED("거절됨");

	private final String label;

	public static InquiryStatus from(String input) {
		return Arrays.stream(values())
			.filter(status -> status.isEqual(input))
			.findAny()
			.orElseThrow(() -> new ValidationException(ChatInquiryErrorCode.NOT_FOUND_STATUS));
	}

	private boolean isEqual(String input) {
		return input.equals(this.label);
	}
}
