package com.dnd.gongmuin.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = " ResponseDTO")
public record LogoutResponse(

	@Schema(description = "로그아웃 성공 시 true, 실패 시 false")
	boolean result
) {
}
