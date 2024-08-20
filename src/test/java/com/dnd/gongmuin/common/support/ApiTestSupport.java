package com.dnd.gongmuin.common.support;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.security.jwt.util.CookieUtil;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.AuthInfo;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;

// 컨트롤러 단 통합테스트용
@SpringBootTest
@AutoConfigureMockMvc
public abstract class ApiTestSupport extends TestContainerSupport {

	protected Member loginMember;
	protected Cookie accessToken;
	@Autowired
	protected MemberRepository memberRepository;
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	protected CookieUtil cookieUtil;

	protected String toJson(Object object) throws JsonProcessingException {
		return objectMapper.writeValueAsString(object);
	}

	// API 테스트할 때마다 Member를 저장하고 토큰정보를 가져오지 않기 위해서 하나의 유저와 토큰정보 구성
	// @PostConstruct
	public void setUpMember() {
		if (loginMember != null) {
			return;
		}
		Member savedMember = memberRepository.save(MemberFixture.member());
		AuthInfo authInfo = AuthInfo.of(
			savedMember.getSocialName(),
			savedMember.getSocialEmail(),
			savedMember.getRole()
		);
		String token = tokenProvider.generateAccessToken(new CustomOauth2User(authInfo), new Date());
		tokenProvider.generateRefreshToken(new CustomOauth2User(authInfo), new Date());
		this.loginMember = savedMember;
		this.accessToken = cookieUtil.createCookie(token);
	}

	@BeforeEach
	void setUp() {
		memberRepository.deleteAll();
		setUpMember();
	}
}
