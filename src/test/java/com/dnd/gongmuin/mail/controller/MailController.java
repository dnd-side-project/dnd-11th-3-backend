package com.dnd.gongmuin.mail.controller;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.mail.dto.request.AuthCodeRequest;
import com.dnd.gongmuin.mail.dto.request.SendMailRequest;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.redis.util.RedisUtil;

@DisplayName("[MailController 통합 테스트]")
@Disabled
public class MailController extends ApiTestSupport {

	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeExpirationMillis;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private RedisUtil redisUtil;

	@AfterEach
	void tearDown() {
		memberRepository.deleteAll();
	}

	@DisplayName("입력 받은 메일 주소로 메일을 발송할 수 있다.")
	@Test
	void sentToMail() throws Exception {
		// given
		SendMailRequest request = new SendMailRequest("gongmuin@korea.kr");

		// then
		mockMvc.perform(post("/api/auth/check-email")
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.toEmail").value(request.toEmail())
			);
	}

	@DisplayName("만료 시간이 지나지 않은 인증 코드를 검증한다.")
	@Test
	void verifyMailAuthCode() throws Exception {
		// given
		AuthCodeRequest request = new AuthCodeRequest("123456", "gongmuin@korea.kr");
		redisUtil.setValues("AuthCode gongmuin@korea.kr", "123456", Duration.ofMillis(authCodeExpirationMillis));

		// then
		mockMvc.perform(post("/api/auth/check-email/{authCode}", request.authCode())
				.content(toJson(request))
				.contentType(APPLICATION_JSON)
				.header(AUTHORIZATION, accessToken)
			)
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.result").value(true)
			);
	}
}
