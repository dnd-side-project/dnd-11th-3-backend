package com.dnd.gongmuin.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthDto {

	private String socialName;
	private String socialEmail;

	@Builder
	private AuthDto(String socialName, String socialEmail) {
		this.socialName = socialName;
		this.socialEmail = socialEmail;
	}
}
