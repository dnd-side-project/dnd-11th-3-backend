package com.dnd.gongmuin.mail.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SendMailRequest(
	@NotBlank(message = "공무원 이메일을 입력해주세요.")
	String toEmail
) {
}
