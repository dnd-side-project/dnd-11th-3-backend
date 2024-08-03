package com.dnd.gongmuin.security.oauth2;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AuthInfo {

	private String socialName;
	private String socialEmail;

	@Builder
	private AuthInfo(String socialName, String socialEmail) {
		this.socialName = socialName;
		this.socialEmail = socialEmail;
	}

	public static AuthInfo fromSocialEmail(String socialEmail) {
		return AuthInfo.builder()
			.socialEmail(socialEmail)
			.build();
	}

	public static AuthInfo of(String socialName, String socialEmail) {
		return AuthInfo.builder()
			.socialName(socialName)
			.socialEmail(socialEmail)
			.build();
	}

}
