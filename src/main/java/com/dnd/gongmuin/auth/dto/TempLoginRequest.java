package com.dnd.gongmuin.auth.dto;

import jakarta.validation.constraints.Email;

public record TempLoginRequest(

	String socialName,

	@Email
	String socialEmail

) {
}
