package com.dnd.gongmuin.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = " ResponseDTO")
public record ReissueResponse(

	@Schema(description = "재발급 된 accessToken")
	String accessToken
) {
}
