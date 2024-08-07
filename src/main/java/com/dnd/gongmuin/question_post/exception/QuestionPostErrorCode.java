package com.dnd.gongmuin.question_post.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionPostErrorCode implements ErrorCode {

	NOT_FOUND_QUESTION_POST("해당 아이디의 질문 포스트가 존재하지 않습니다.", "QP_001"),
	NOT_ENOUGH_CREDIT("보유한 크레딧이 부족합니다.", "QP_002");

	private final String message;
	private final String code;
}
