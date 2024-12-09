package com.dnd.gongmuin.answer.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnswerErrorCode implements ErrorCode {

	NOT_FOUND_ANSWER("해당 아이디의 답변이 존재하지 않습니다.", "ANS_001"),
	ALREADY_CHOSEN_ANSWER_EXISTS("채택한 답변이 존재합니다.", "ANS_02"),
	NOT_REGISTER_ANSWER("답변을 등록할 수 없습니다.", "ANS_003");

	private final String message;
	private final String code;
}
