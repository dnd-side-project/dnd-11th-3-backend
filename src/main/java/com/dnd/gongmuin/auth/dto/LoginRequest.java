package com.dnd.gongmuin.auth.dto;

import jakarta.validation.constraints.Email;

public record LoginRequest(

	String socialName,

	@Email
	String socialEmail

) {
}
