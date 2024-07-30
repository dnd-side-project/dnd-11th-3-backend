package com.dnd.gongmuin.s3.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum S3ErrorCode implements ErrorCode {
	EMPTY_FILE_NAME("원본 파일명은 필수입니다.","S3_001"),

	UNSUPPORTED_FILE_TYPE("지원하지 않는 파일 형식입니다.","S3_002"),

	FAILED_TO_UPLOAD("s3에 파일을 업로드하는데 실패했습니다.", "S3_003"),
	INVALID_FILE_EXTENSION("파일 형식이 올바르지 않습니다.", "S4_004");

	private final String message;
	private final String code;
}
