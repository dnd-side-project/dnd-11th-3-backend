package com.dnd.gongmuin.auth.dto;

public record LoginRequest(

	String socialName,
	String socialEmail

) {
}
