package com.dnd.gongmuin.auth.dto.request;

import jakarta.validation.constraints.Email;

public record TempSignUpRequest(

	String socialName,

	@Email
	String socialEmail

) {
}
