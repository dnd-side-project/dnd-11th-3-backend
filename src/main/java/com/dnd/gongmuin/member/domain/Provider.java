package com.dnd.gongmuin.member.domain;

import java.util.Arrays;

import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {

	KAKAO("kakao"),
	NAVER("naver");

	private final String label;

	public static Provider fromProviderName(String providerName) {
		return Arrays.stream(values())
			.filter(provider -> provider.getLabel().equalsIgnoreCase(providerName))
			.findFirst()
			.orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_PROVIDER));
	}

	public static Provider fromSocialEmail(String socialEmail) {
		return Arrays.stream(values())
			.filter(provider -> socialEmail.contains(provider.getLabel()))
			.findFirst()
			.orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_PROVIDER));
	}
}
