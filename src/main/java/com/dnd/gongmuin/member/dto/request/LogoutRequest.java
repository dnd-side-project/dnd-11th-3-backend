package com.dnd.gongmuin.member.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LogoutRequest(
	@NotEmpty(message = "잘못된 요청입니다.")
	String accessToken
) {

}
