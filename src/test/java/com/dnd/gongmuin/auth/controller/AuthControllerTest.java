package com.dnd.gongmuin.auth.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.dnd.gongmuin.auth.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.auth.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.AuthInfo;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

import jakarta.servlet.http.Cookie;

@DisplayName("[AuthController] 통합테스트")
class AuthControllerTest extends ApiTestSupport {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private TokenProvider tokenProvider;

	@AfterEach
	void tearDown() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();

	}

	@DisplayName("닉네임 중복을 검증한다.")
	@Test
	void checkNickName() throws Exception {
		// given
		Member member = MemberFixture.member2();
		memberRepository.save(member);
		ValidateNickNameRequest request = new ValidateNickNameRequest("회원");

		// when  // then
		mockMvc.perform(post("/api/auth/check-nickname")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("isDuplicated").value(true));
	}

	@DisplayName("추가 정보를 기입해 회원가입을 진행한다.")
	@Test
	void signUp() throws Exception {
		// given
		AdditionalInfoRequest request = new AdditionalInfoRequest("dsaf@korea.kr", "회원", "공업", "일반기계");

		Member savedMember = memberRepository.save(MemberFixture.member3());
		AuthInfo authInfo = AuthInfo.of(
			savedMember.getSocialName(),
			savedMember.getSocialEmail(),
			savedMember.getRole()
		);
		String token = tokenProvider.generateAccessToken(new CustomOauth2User(authInfo), new Date());
		this.loginMember = savedMember;
		this.accessToken = new Cookie("Authorization", token);

		// when  // then
		mockMvc.perform(post("/api/auth/member")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("nickName").value("회원"));
	}

	@DisplayName("로그인 된 회원은 로그아웃을 할 수 있다.")
	@Test
	void logout() throws Exception {
		// given

		// when  // then
		mockMvc.perform(post("/api/auth/logout")
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("result").value("true"));
	}

	@DisplayName("토큰을 재발급 할 수 있다.")
	@Test
	void reissue() throws Exception {
		// given

		// when  // then
		mockMvc.perform(post("/api/auth/reissue/token")
				.contentType(APPLICATION_JSON)
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(cookie().exists("Authorization"));
	}
}
