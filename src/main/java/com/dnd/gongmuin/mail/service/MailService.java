package com.dnd.gongmuin.mail.service;

import java.util.Objects;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.mail.dto.MailMapper;
import com.dnd.gongmuin.mail.dto.request.SendMailRequest;
import com.dnd.gongmuin.mail.dto.response.SendMailResponse;
import com.dnd.gongmuin.mail.exception.MailErrorCode;
import com.dnd.gongmuin.mail.util.AuthCodeGenerator;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

	private final String SUBJECT = "[공무인] 공무원 인증 메일입니다.";
	private final JavaMailSender mailSender;
	private final AuthCodeGenerator authCodeGenerator;

	public SendMailResponse sendEmail(SendMailRequest request) {
		String toEmail = request.toEmail();
		MimeMessage email = createMail(toEmail);
		mailSender.send(email);

		return MailMapper.toSendMailResponse(toEmail);
	}

	private MimeMessage createMail(String toEmail) {
		try {
			String authCode = authCodeGenerator.createAuthCode();

			if (Objects.isNull(authCode) || authCode.isBlank()) {
				throw new IllegalArgumentException();
			}

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
			messageHelper.setTo(toEmail);
			messageHelper.setSubject(SUBJECT);
			messageHelper.setText("인증 코드는 다음과 같습니다.\n" + authCode);

			// TODO : Redis에 생성된 인증코드 저장(인증 시간 설정)

			return mimeMessage;
		} catch (IllegalArgumentException e) {
			throw new NotFoundException(MailErrorCode.MAIL_CONTENT_ERROR);
		} catch (Exception e) {
			throw new NotFoundException(MailErrorCode.MAIL_CONFIGURATION_ERROR);
		}
	}

	// TODO : 인증 코드 검증
	// TODO : 특정 공무원 이메일 검증
}
