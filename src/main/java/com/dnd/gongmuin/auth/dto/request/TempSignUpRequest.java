package com.dnd.gongmuin.auth.dto.request;

import jakarta.validation.constraints.Email;

public record TempLoginRequest(

	String socialName,

	@Email
	String socialEmail

) {
}
