package com.dnd.gongmuin.mail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.mail.dto.request.SendMailRequest;
import com.dnd.gongmuin.mail.dto.response.SendMailResponse;
import com.dnd.gongmuin.mail.service.MailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/check-email")
public class MailController {

	private final MailService mailService;

	public ResponseEntity<SendMailResponse> mailSend(@RequestBody SendMailRequest sendMailRequest) {
		SendMailResponse toEmail = mailService.sendEmail(sendMailRequest);
		return ResponseEntity.ok(toEmail);
	}

	// TODO : 인증 코드 검증
}
