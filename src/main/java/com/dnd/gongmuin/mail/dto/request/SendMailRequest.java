package com.dnd.gongmuin.mail.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 인증 코드 요청 RequestDTO")
public record SendMailRequest(
	@NotBlank(message = "공무원 이메일을 입력해주세요.")
	@Email
	@Schema(description = "수신 받을 공무원 이메일", example = "gongmuin@korea.kr")
	String targetEmail
) {
}
