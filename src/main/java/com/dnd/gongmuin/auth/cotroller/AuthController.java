package com.dnd.gongmuin.auth.cotroller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.auth.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.auth.dto.request.TempLoginRequest;
import com.dnd.gongmuin.auth.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.auth.dto.response.LogoutResponse;
import com.dnd.gongmuin.auth.dto.response.ReissueResponse;
import com.dnd.gongmuin.auth.dto.response.SignUpResponse;
import com.dnd.gongmuin.auth.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.auth.service.AuthService;
import com.dnd.gongmuin.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Social Login API", description = "소셜 로그인 요청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "임시 회원가입(토큰 발급) API", description = "로그인 또는 회원가입 후 토큰을 발급한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/token")
	public ResponseEntity<String> getTempToken(
		@RequestBody @Valid TempLoginRequest request,
		HttpServletResponse response) {
		authService.swaggerToken(request, response);
		return ResponseEntity.ok("성공");

	}

	@Operation(summary = "닉네임 중복 검증 API", description = "닉네임 중복을 검증한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/check-nickname")
	public ResponseEntity<ValidateNickNameResponse> checkNickName(
		@RequestBody @Valid ValidateNickNameRequest request) {
		return ResponseEntity.ok(authService.isDuplicatedNickname(request));
	}

	@Operation(summary = "추가정보 API", description = "추가 정보를 저장한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/member")
	public ResponseEntity<SignUpResponse> signUp(
		@RequestBody @Valid AdditionalInfoRequest request,
		@AuthenticationPrincipal Member loginMember) {
		SignUpResponse response = authService.signUp(request, loginMember.getSocialEmail());

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "로그아웃 API", description = "로그아웃한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/logout")
	public ResponseEntity<LogoutResponse> logout(HttpServletRequest request) {
		LogoutResponse response = authService.logout(request);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "토큰 재발급 API", description = "토큰을 재발급한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/reissue/token")
	public ResponseEntity<ReissueResponse> reissue(HttpServletRequest request, HttpServletResponse response) {
		ReissueResponse reissueResponse = authService.reissue(request, response);

		return ResponseEntity.ok(reissueResponse);
	}
}

