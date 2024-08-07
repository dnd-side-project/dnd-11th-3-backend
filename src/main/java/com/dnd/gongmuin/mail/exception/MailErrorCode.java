package com.dnd.gongmuin.mail.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode {

	MAIL_CONFIGURATION_ERROR("메일 설정 오류입니다.", "MAIL_001"),
	MAIL_CONTENT_ERROR("인증 코드 생성에 실패했습니다.", "MAIL_002"),
	MAIL_DUPLICATED_ERROR("이미 존재하는 공무원 이메일입니다.", "MAIL_003");

	private final String message;
	private final String code;
}
