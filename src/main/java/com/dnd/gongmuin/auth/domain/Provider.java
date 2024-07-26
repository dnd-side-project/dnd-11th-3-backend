package com.dnd.gongmuin.auth.domain;

import java.util.Arrays;

import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {

	KAKAO("카카오"),
	NAVER("네이버");

	private final String provider;

	public static Provider fromProviderName(String providerName) {
		return Arrays.stream(values())
			.filter(provider -> provider.getProvider().equalsIgnoreCase(providerName))
			.findFirst()
			.orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_PROVIDER));
	}
}
