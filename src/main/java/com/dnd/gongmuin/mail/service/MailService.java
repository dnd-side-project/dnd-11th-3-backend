package com.dnd.gongmuin.mail.service;

import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.mail.dto.MailMapper;
import com.dnd.gongmuin.mail.dto.request.AuthCodeRequest;
import com.dnd.gongmuin.mail.dto.request.SendMailRequest;
import com.dnd.gongmuin.mail.dto.response.AuthCodeResponse;
import com.dnd.gongmuin.mail.dto.response.SendMailResponse;
import com.dnd.gongmuin.mail.exception.MailErrorCode;
import com.dnd.gongmuin.mail.util.AuthCodeGenerator;
import com.dnd.gongmuin.member.service.MemberService;
import com.dnd.gongmuin.redis.util.RedisUtil;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	@Value("${spring.mail.auth-code-expiration-millis}")
	private long authCodeExpirationMillis;
	private final String SUBJECT = "[공무인] 공무원 인증 메일입니다.";
	private static final String AUTH_CODE_PREFIX = "AuthCode ";

	private final JavaMailSender mailSender;
	private final AuthCodeGenerator authCodeGenerator;
	private final RedisUtil redisUtil;
	private final MemberService memberService;

	public SendMailResponse sendEmail(SendMailRequest request) {
		String toEmail = request.toEmail();

		checkForDuplicatedOfficialEmail(toEmail);
		MimeMessage email = createMail(toEmail);
		mailSender.send(email);

		return MailMapper.toSendMailResponse(toEmail);
	}

	public AuthCodeResponse verifyMailAuthCode(AuthCodeRequest authCodeRequest) {
		String toEmail = AUTH_CODE_PREFIX + authCodeRequest.toEmail();
		String authCode = authCodeRequest.authCode();

		boolean result = redisUtil.validateData(toEmail, authCode);

		return MailMapper.toAuthCodeResponse(result);
	}

	private MimeMessage createMail(String toEmail) {
		try {
			String authCode = authCodeGenerator.createAuthCode();

			if (Objects.isNull(authCode) || authCode.isBlank()) {
				throw new IllegalArgumentException();
			}

			saveAuthCodeToRedis(toEmail, authCode, authCodeExpirationMillis);

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			messageHelper.setTo(toEmail);
			messageHelper.setSubject(SUBJECT);
			messageHelper.setText("인증 코드는 다음과 같습니다.\n" + authCode);

			return mimeMessage;
		} catch (IllegalArgumentException e) {
			throw new NotFoundException(MailErrorCode.MAIL_CONTENT_ERROR);
		} catch (Exception e) {
			throw new NotFoundException(MailErrorCode.MAIL_CONFIGURATION_ERROR);
		}
	}

	private void saveAuthCodeToRedis(String toEmail, String authCode, long authCodeExpirationMillis) {
		String key = AUTH_CODE_PREFIX + toEmail;
		redisUtil.setValues(key, authCode, Duration.ofMillis(authCodeExpirationMillis));
	}

	private void checkForDuplicatedOfficialEmail(String officialEmail) {
		if (memberService.isOfficialEmailExists(officialEmail)) {
			throw new NotFoundException(MailErrorCode.MAIL_DUPLICATED_ERROR);
		}
	}
}
