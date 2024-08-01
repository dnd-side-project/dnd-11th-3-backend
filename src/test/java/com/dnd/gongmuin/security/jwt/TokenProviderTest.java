package com.dnd.gongmuin.security.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.dto.AuthDto;
import com.dnd.gongmuin.security.dto.CustomOauth2User;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Transactional
@SpringBootTest
class TokenProviderTest {

	@InjectMocks
	private TokenProvider tokenProvider;

	@Mock
	private TokenService tokenService;

	private SecretKey secretKey;

	@Mock
	private AuthDto authDto;

	@Value("${spring.jwt.key}")
	private String key;

	@BeforeEach
	void setUp() {
		openMocks(this);

		secretKey = Keys.hmacShaKeyFor(key.getBytes());

		ReflectionTestUtils.setField(tokenProvider, "secretKey", secretKey);
	}

	@DisplayName("만료일이 30분인 토큰이 생성된다.")
	@Test
	void generateAccessToken() {
		// given
		Date now = new Date();
		long expectedExpirationTime = now.getTime() + 30 * 60 * 1000;

		when(authDto.getSocialEmail()).thenReturn("kakao123/kimMember@daum.net");
		when(authDto.getSocialName()).thenReturn("김회원");
		CustomOauth2User authentication = new CustomOauth2User(authDto);

		// when
		String accessToken = tokenProvider.generateAccessToken(authentication, now);
		Claims claims = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();

		Date expiration = claims.getExpiration();

		// then
		assertThat(expiration.getTime()).isCloseTo(expectedExpirationTime, within(1000L));
	}

	@DisplayName("만료일이 1일인 토큰이 생성된다.")
	@Test
	void generateRefreshToken() {
		// given
		Date now = new Date();
		long expectedExpirationTime = now.getTime() + 1000 * 60 * 60 * 24;

		when(authDto.getSocialEmail()).thenReturn("kakao123/kimMember@daum.net");
		when(authDto.getSocialName()).thenReturn("김회원");
		CustomOauth2User authentication = new CustomOauth2User(authDto);

		// when
		String accessToken = tokenProvider.generateRefreshToken(authentication, now);
		Claims claims = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();

		Date expiration = claims.getExpiration();

		// then
		assertThat(expiration.getTime()).isCloseTo(expectedExpirationTime, within(1000L));
	}

	@DisplayName("토큰 파싱을 통해 만들어진 인증 객체의 이메일은 토큰 정보의 이메일 값과 동일하다.")
	@Test
	void getAuthentication() {
		// given
		Date now = new Date();
		long expectedExpirationTime = now.getTime() + 1000 * 60 * 60 * 24;

		when(authDto.getSocialEmail()).thenReturn("kakao123/kimMember@daum.net");
		when(authDto.getSocialName()).thenReturn("김회원");
		CustomOauth2User customOauth2User = new CustomOauth2User(authDto);
		String accessToken = tokenProvider.generateRefreshToken(customOauth2User, now);

		// when
		Authentication authentication = tokenProvider.getAuthentication(accessToken);
		CustomOauth2User getPrincipal = (CustomOauth2User)authentication.getPrincipal();

		// then
		assertThat(authentication.isAuthenticated()).isTrue();
		assertThat(getPrincipal.getEmail()).isEqualTo("kakao123/kimMember@daum.net");
	}

	@DisplayName("토큰의 만료일이 현재 시간보다 전이면 만료된 토큰이다.")
	@Test
	void validateToken() {
		// given
		Date past = new Date(124, 6, 30, 16, 0, 0);

		when(authDto.getSocialEmail()).thenReturn("kakao123/kimMember@daum.net");
		when(authDto.getSocialName()).thenReturn("김회원");
		CustomOauth2User customOauth2User = new CustomOauth2User(authDto);
		String accessToken = tokenProvider.generateRefreshToken(customOauth2User, past);

		// when
		boolean result = tokenProvider.validateToken(accessToken, new Date());

		// then
		assertThat(result).isFalse();
	}
}

