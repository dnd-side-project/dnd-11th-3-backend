package com.dnd.gongmuin.auth.dto.request;

import jakarta.validation.constraints.Email;

public record TempSignInRequest(
	@Email
	String socialEmail

) {
}
