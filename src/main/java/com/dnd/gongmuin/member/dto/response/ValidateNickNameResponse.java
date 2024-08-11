package com.dnd.gongmuin.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = " ResponseDTO")
public record ValidateNickNameResponse(

	@Schema(description = "중복이 아니면 true, 중복이면 false")
	boolean isDuplicated
) {
}
