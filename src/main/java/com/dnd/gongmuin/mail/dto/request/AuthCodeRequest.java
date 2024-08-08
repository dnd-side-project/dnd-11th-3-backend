package com.dnd.gongmuin.mail.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AuthCodeRequest(
	@NotBlank(message = "인증 코드를 입력해주세요.")
	@Pattern(regexp = "\\d{6}", message = "인증 코드는 6자리 숫자여야 합니다.")
	String authCode,

	@NotBlank(message = "공무원 이메일을 입력해주세요.")
	@Email
	String targetEmail
) {
}
