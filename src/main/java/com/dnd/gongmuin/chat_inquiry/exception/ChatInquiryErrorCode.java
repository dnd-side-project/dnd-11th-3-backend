package com.dnd.gongmuin.chat_inquiry.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatInquiryErrorCode implements ErrorCode {

	NOT_FOUND_INQUIRY("해당 아이디의 채팅 요청이 존재하지 않습니다.", "CI_001"),
	UNAUTHORIZED_REQUEST("채팅 수락을 하거나 거절할 권한이 없습니다.", "CI_002"),
	UNABLE_TO_CHANGE_STATUS("이미 수락했거나 거절한 요청입니다.", "CI_003");

	private final String message;
	private final String code;
}
