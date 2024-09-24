package com.dnd.gongmuin.security.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.redis.util.RedisUtil;
import com.dnd.gongmuin.security.exception.OAuth2ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2UnlinkService {

	private static final String KAKAO_URL = "https://kapi.kakao.com/v1/user/unlink";
	private static final String NAVER_URL = "https://nid.naver.com/oauth2.0/token";
	private final RestTemplate restTemplate;
	private final RedisUtil redisUtil;
	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String NAVER_CLIENT_ID;
	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String NAVER_CLIENT_SECRET;

	public void unlink(String provider) {
		if (provider.startsWith("kakao")) {
			kakaoUnlink(provider);
		} else if (provider.startsWith("naver")) {
			naverUnlink(provider);
		} else {
			throw new ValidationException(OAuth2ErrorCode.INVALID_REQUEST);
		}
	}

	public void kakaoUnlink(String provider) {
		String accessToken = redisUtil.getValues("AT(oauth):" + provider);
		// oauth2 토큰이 만료 시 재 로그인
		if (accessToken == null) {
			throw new ValidationException(OAuth2ErrorCode.EXPIRED_AUTH_TOKEN);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		HttpEntity<Object> entity = new HttpEntity<>("", headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(
			KAKAO_URL,
			HttpMethod.POST,
			entity,
			String.class
		);

		if (responseEntity.getBody().isEmpty()) {
			throw new ValidationException(OAuth2ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public void naverUnlink(String provider) {
		String accessToken = redisUtil.getValues("AT(oauth):" + provider);

		// oauth2 토큰이 만료 시 재 로그인
		if (accessToken == null) {
			throw new ValidationException(OAuth2ErrorCode.EXPIRED_AUTH_TOKEN);
		}

		String url = NAVER_URL +
			"?service_provider=NAVER" +
			"&grant_type=delete" +
			"&client_id=" +
			NAVER_CLIENT_ID +
			"&client_secret=" +
			NAVER_CLIENT_SECRET +
			"&access_token=" +
			accessToken;

		NaverUnlinkResponse response = restTemplate.getForObject(url, NaverUnlinkResponse.class);

		if (response != null && !"success".equalsIgnoreCase(response.getResult())) {
			throw new ValidationException(OAuth2ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 네이버 응답 데이터
	 */
	@Getter
	public static class NaverUnlinkResponse {
		private final String accessToken;
		private final String result;

		@JsonCreator
		public NaverUnlinkResponse(
			@JsonProperty("access_token") String accessToken,
			@JsonProperty("result") String result) {
			this.accessToken = accessToken;
			this.result = result;
		}
	}
}
