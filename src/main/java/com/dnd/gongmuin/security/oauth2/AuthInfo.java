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

	public static AuthDto fromSocialEmail(String socialEmail) {
		return AuthDto.builder()
			.socialEmail(socialEmail)
			.build();
	}

	public static AuthDto of(String socialName, String socialEmail) {
		return new AuthDto(socialName, socialEmail);
	}

}
