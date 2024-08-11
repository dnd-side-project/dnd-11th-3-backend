package com.dnd.gongmuin.mail.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "이메일 인증 코드 응답 ResponseDTO")
public record SendMailResponse(
	@Schema(description = "발송 된 공무원 이메일", example = "gongmuin@korea.kr")
	String targetEmail
) {
}
