package com.dnd.gongmuin.member.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record LogoutRequest(

	@NotEmpty(message = "AccessToken을 입력해주세요.")
	String accessToken
) {

}
