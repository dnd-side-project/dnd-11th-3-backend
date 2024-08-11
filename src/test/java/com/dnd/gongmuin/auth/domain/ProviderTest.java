package com.dnd.gongmuin.auth.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProviderTest {

	@DisplayName("Provider 이름으로 해당 Enum을 찾을 수 있다.")
	@Test
	void fromProviderName() {
		// given
		String kakaoProviderName = "kakao";
		String naverProviderName = "naver";

		// when
		Provider findProvider1 = Provider.fromProviderName(kakaoProviderName);
		Provider findProvider2 = Provider.fromProviderName(naverProviderName);

		// then
		assertThat(findProvider1).isEqualTo(Provider.KAKAO);
		assertThat(findProvider2).isEqualTo(Provider.NAVER);
	}

	@DisplayName("소셜 이메일로 해당 Enum을 찾을 수 있다.")
	@Test
	void fromSocialEmail() {
		// given
		String kakaoProviderName = "kakao123/kim@daum.net";
		String naverProviderName = "naver123/park@naver.com";

		// when
		Provider findProvider1 = Provider.fromSocialEmail(kakaoProviderName);
		Provider findProvider2 = Provider.fromSocialEmail(naverProviderName);

		// then
		assertThat(findProvider1).isEqualTo(Provider.KAKAO);
		assertThat(findProvider2).isEqualTo(Provider.NAVER);
	}

}
