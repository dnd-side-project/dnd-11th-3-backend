package com.dnd.gongmuin.mail.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 코드 검증 응답 ResponseDTO")
public record AuthCodeResponse(
	@Schema(description = "성공 시 true, 실패 시 false 반환")
	boolean result
) {
}
