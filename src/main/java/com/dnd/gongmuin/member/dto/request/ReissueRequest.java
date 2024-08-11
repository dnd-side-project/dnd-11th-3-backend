package com.dnd.gongmuin.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "토큰 재발급 RequestDTO")
public record ReissueRequest(

	@NotEmpty(message = "AccessToken을 입력해주세요.")
	@Schema(description = "이전에 발급된 AccessToken")
	String accessToken
) {
}
