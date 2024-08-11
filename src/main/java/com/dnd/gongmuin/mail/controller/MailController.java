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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Mail API", description = "메일 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/check-email")
public class MailController {

	private final MailService mailService;

	@Operation(summary = "공무원 이메일 인증 코드 요청 API",
		description = "요청 받은 이메일 중복 가입 여부를 검증하고, 해당 이메일로 인증 코드 이메일 발송한다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "인증 코드 요청 성공", content = {
			@Content(schema = @Schema(implementation = SendMailResponse.class))
		}),
		@ApiResponse(responseCode = "400", description = "이미 존재하는 공무원 이메일, 발송 메일 설정 오류, 인증 코드 생성 실패")
	})
	@PostMapping
	public ResponseEntity<SendMailResponse> sendAuthCodeToMail(
		@RequestBody @Valid SendMailRequest sendMailRequest) {
		SendMailResponse targetEmail = mailService.sendEmail(sendMailRequest);
		return ResponseEntity.ok(targetEmail);
	}

	@Operation(summary = "공무원 이메일 인증 코드 검증 API", description = "인증 코드의 일치 여부를 검증한다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "인증 코드 검증 성공", content = {
			@Content(schema = @Schema(implementation = AuthCodeResponse.class))
		}),
		@ApiResponse(responseCode = "400", description = "만료된 인증 코드, 인증 코드 검증 오류")
	})
	@PostMapping("/authCode")
	public ResponseEntity<AuthCodeResponse> verifyMailAuthCode(
		@RequestBody @Valid AuthCodeRequest authCodeRequest) {
		AuthCodeResponse authCodeResponse = mailService.verifyMailAuthCode(authCodeRequest);
		return ResponseEntity.ok(authCodeResponse);
	}
}
