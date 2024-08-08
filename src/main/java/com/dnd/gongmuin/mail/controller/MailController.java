package com.dnd.gongmuin.mail.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.mail.dto.request.AuthCodeRequest;
import com.dnd.gongmuin.mail.dto.request.SendMailRequest;
import com.dnd.gongmuin.mail.dto.response.AuthCodeResponse;
import com.dnd.gongmuin.mail.dto.response.SendMailResponse;
import com.dnd.gongmuin.mail.service.MailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/check-email")
public class MailController {

	private final MailService mailService;

	@PostMapping
	public ResponseEntity<SendMailResponse> sendAuthCodeToMail(
		@RequestBody @Valid SendMailRequest sendMailRequest) {
		SendMailResponse targetEmail = mailService.sendEmail(sendMailRequest);
		return ResponseEntity.ok(targetEmail);
	}

	@PostMapping("/authCode")
	public ResponseEntity<AuthCodeResponse> verifyMailAuthCode(
		@RequestBody @Valid AuthCodeRequest authCodeRequest) {
		AuthCodeResponse authCodeResponse = mailService.verifyMailAuthCode(authCodeRequest);
		return ResponseEntity.ok(authCodeResponse);
	}
}
