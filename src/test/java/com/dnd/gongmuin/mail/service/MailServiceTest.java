package com.dnd.gongmuin.mail.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import com.dnd.gongmuin.redis.util.RedisUtil;

import jakarta.mail.internet.MimeMessage;

@DisplayName("[MailService 테스트]")
@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private MemberService memberService;

	@Mock
	private AuthCodeGenerator authCodeGenerator;

	@Mock
	private JavaMailSender mailSender;

	@Mock
	private RedisUtil redisUtil;

	@Mock
	private RedisTemplate redisTemplate;

	@InjectMocks
	private MailService mailService;

	@DisplayName("인증 코드 이메일을 발송할 수 있다.")
	@Test
	void sendToEmail() {
		// given
		SendMailRequest request = new SendMailRequest("gongmuin@korea.kr");
		MimeMessage mimeMessage = mock(MimeMessage.class);

		String authCode = "123456";
		when(authCodeGenerator.createAuthCode()).thenReturn(authCode);
		when(memberService.isOfficialEmailExists(anyString())).thenReturn(false);
		when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

		// when
		SendMailResponse response = mailService.sendEmail(request);

		// then
		assertThat(request.toEmail()).isEqualTo(response.toEmail());
	}

	@DisplayName("발송된 인증번호와 입력된 인증번호를 검증한다.")
	@Test
	void verifyAuthCode() {
		// given
		String toEmail = "gongmuin@korea.kr";
		String authCode = "123456";
		String SUBJECT = "[공무인] 공무원 인증 메일입니다.";
		AuthCodeRequest request = new AuthCodeRequest(authCode, toEmail);

		String key = SUBJECT + toEmail;
		when(redisUtil.validateData(key, authCode)).thenReturn(true);

		// when
		AuthCodeResponse response = mailService.verifyMailAuthCode(request);

		// then
		assertThat(response.result()).isTrue();
	}

	@DisplayName("만료된 인증번호를 검증할 수 없다.")
	@Test
	void verifyAuthCodeWithExpired() {
		// given
		String toEmail = "gongmuin@korea.kr";
		String authCode = "123456";
		String SUBJECT = "[공무인] 공무원 인증 메일입니다.";
		AuthCodeRequest request = new AuthCodeRequest(authCode, toEmail);

		String key = SUBJECT + toEmail;
		when(redisUtil.validateData(key, authCode)).thenThrow(NotFoundException.class);

		// when // then
		assertThrowsExactly(NotFoundException.class, () -> mailService.verifyMailAuthCode(request));
	}

}
