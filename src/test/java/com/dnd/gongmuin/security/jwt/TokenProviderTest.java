package com.dnd.gongmuin.security.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.redis.util.RedisUtil;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.AuthInfo;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

	private String key;
	private SecretKey secretKey;
	private AuthInfo authInfo;

	@Mock
	private RedisUtil redisUtil;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private TokenProvider tokenProvider;

	@BeforeEach
	void setUp() {
		key = "oeq213n214eqf141n161saf145125t12tg2er31t3241g4v2r3131351332dsafsawefewqrft23fewfvdsafdsf32e1wq1r3ewfedfasdfsdafqrewqr1";
		secretKey = Keys.hmacShaKeyFor(key.getBytes());

		ReflectionTestUtils.setField(tokenProvider, "secretKey", secretKey);

		this.authInfo = AuthInfo.of("김회원", "kakao123/daum.net", "ROLE_USER");
	}

	@DisplayName("만료일이 1시간 30분인 토큰이 생성된다.")
	@Test
	void generateAccessToken() {
		// given
		Date now = new Date();
		long expectedExpirationTime = now.getTime() + 90 * 60 * 1000;

		CustomOauth2User authentication = new CustomOauth2User(authInfo);

		// when
		String accessToken = tokenProvider.generateAccessToken(authentication, now);
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload();
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

		CustomOauth2User authentication = new CustomOauth2User(authInfo);

		// when
		String accessToken = tokenProvider.generateRefreshToken(authentication, now);
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(accessToken).getPayload();
		Date expiration = claims.getExpiration();

		// then
		assertThat(expiration.getTime()).isCloseTo(expectedExpirationTime, within(1000L));
	}

	@DisplayName("토큰 파싱을 통해 만들어진 인증 객체의 이메일은 토큰 정보의 이메일 값과 동일하다.")
	@Test
	void getAuthentication() {
		// given
		Date now = new Date();

		Member member = MemberFixture.member();
		CustomOauth2User customOauth2User = new CustomOauth2User(authInfo);
		String accessToken = tokenProvider.generateAccessToken(customOauth2User, now);

		given(memberRepository.findBySocialEmail(anyString())).willReturn(Optional.ofNullable(member));

		// when
		Authentication authentication = tokenProvider.getAuthentication(accessToken);
		Member principal = (Member)authentication.getPrincipal();

		// then
		assertThat(authentication.isAuthenticated()).isTrue();
		assertThat(principal.getSocialEmail()).isEqualTo("KAKAO123/gongmuin@daum.net");
	}

	@DisplayName("토큰의 만료일이 현재 시간보다 전이면 만료된 토큰이다.")
	@Test
	void validateToken() {
		// given
		Date past = new Date(124, 6, 30, 16, 0, 0);

		CustomOauth2User customOauth2User = new CustomOauth2User(authInfo);
		String accessToken = tokenProvider.generateRefreshToken(customOauth2User, past);

		// when
		boolean result = tokenProvider.validateToken(accessToken, new Date());

		// then
		assertThat(result).isFalse();
	}
}
