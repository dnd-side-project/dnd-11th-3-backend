package com.dnd.gongmuin.chat.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {

	INVALID_MESSAGE_TYPE("메시지 타입을 올바르게 입력해주세요.", "CH_001"),
	NOT_FOUND_CHAT_ROOM("해당 아이디의 채팅방이 존재하지 않습니다.", "CH_002");

	private final String message;
	private final String code;
}
