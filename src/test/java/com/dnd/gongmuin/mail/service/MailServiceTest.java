package com.dnd.gongmuin.mail.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.mail.dto.request.AuthCodeRequest;
import com.dnd.gongmuin.mail.dto.request.SendMailRequest;
import com.dnd.gongmuin.mail.dto.response.AuthCodeResponse;
import com.dnd.gongmuin.mail.dto.response.SendMailResponse;
import com.dnd.gongmuin.mail.util.AuthCodeGenerator;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.member.service.MemberService;
import com.dnd.gongmuin.redis.exception.RedisErrorCode;
import com.dnd.gongmuin.redis.util.RedisUtil;

import jakarta.mail.internet.MimeMessage;

@DisplayName("[MailService 테스트]")
@ExtendWith(MockitoExtension.class)
class MailServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MemberService memberService;

	@Mock
	private AuthCodeGenerator authCodeGenerator;

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private RedisUtil redisUtil;

	@InjectMocks
	private MailService mailService;

	@DisplayName("인증 코드 이메일을 발송할 수 있다.")
	@Test
	void sendToEmail() {
		// given
		SendMailRequest request = new SendMailRequest("gongmuin@korea.kr");
		MimeMessage mimeMessage = mock(MimeMessage.class);

		String authCode = "123456";
		given(authCodeGenerator.createAuthCode()).willReturn(authCode);
		given(memberService.isOfficialEmailExists(anyString())).willReturn(false);
		given(mailSender.createMimeMessage()).willReturn(mimeMessage);

		// when
		SendMailResponse response = mailService.sendEmail(request);

		// then
		assertThat(request.targetEmail()).isEqualTo(response.targetEmail());
	}

	@DisplayName("발송된 인증번호와 입력된 인증번호를 검증한다.")
	@Test
	void verifyAuthCode() {
		// given
		String toEmail = "gongmuin@korea.kr";
		String authCode = "123456";
		String PREFIX = "AuthCode ";
		AuthCodeRequest request = new AuthCodeRequest(authCode, toEmail);

		String key = PREFIX + toEmail;
		given(redisUtil.validateData(key, authCode)).willReturn(true);

		// when
		AuthCodeResponse response = mailService.verifyMailAuthCode(request);

		// then
		assertThat(response.result()).isTrue();
	}

	@DisplayName("만료된 인증번호를 검증할 수 없다.")
	@Test
	void verifyAuthCodeWithExpired() {
		// given
		String targetEmail = "gongmuin@korea.kr";
		String authCode = "123456";
		String PREFIX = "AuthCode ";
		AuthCodeRequest request = new AuthCodeRequest(authCode, targetEmail);

		String key = PREFIX + targetEmail;

		doThrow(new NotFoundException(RedisErrorCode.REDIS_EXPIRED_ERROR))
			.when(redisUtil).validateExpiredFromKey(key);

		// when // then
		assertThatThrownBy(() -> mailService.verifyMailAuthCode(request))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(RedisErrorCode.REDIS_EXPIRED_ERROR.getMessage());
	}

}
