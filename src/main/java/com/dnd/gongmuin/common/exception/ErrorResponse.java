package com.dnd.gongmuin.common.exception;

public record ErrorResponse(

	String message,
	String code
) {
}
