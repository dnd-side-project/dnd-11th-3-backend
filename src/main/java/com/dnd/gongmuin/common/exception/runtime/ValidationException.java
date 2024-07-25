package com.dnd.gongmuin.common.exception.runtime;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

	private final String code;

	public ValidationException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.code = errorCode.getCode();
	}

	public ValidationException(String message) {
		super(message);
		this.code = "MSG_000";
	}
}
