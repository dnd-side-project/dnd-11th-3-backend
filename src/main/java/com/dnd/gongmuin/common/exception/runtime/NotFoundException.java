package com.dnd.gongmuin.common.exception.runtime;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

	private final String code;

	public NotFoundException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.code = errorCode.getCode();
	}
}
