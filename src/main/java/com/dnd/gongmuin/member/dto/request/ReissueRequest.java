package com.dnd.gongmuin.member.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ReissueRequest(

	@NotEmpty(message = "AccessToken을 입력해주세요.")
	String accessToken
) {
}
