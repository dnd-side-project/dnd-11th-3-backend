package com.dnd.gongmuin.auth.cotroller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.auth.dto.TempLoginRequest;
import com.dnd.gongmuin.auth.service.AuthService;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.LogoutRequest;
import com.dnd.gongmuin.member.dto.request.ReissueRequest;
import com.dnd.gongmuin.member.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.member.dto.response.LogoutResponse;
import com.dnd.gongmuin.member.dto.response.ReissueResponse;
import com.dnd.gongmuin.member.dto.response.SignUpResponse;
import com.dnd.gongmuin.member.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Social Login API", description = "소셜 로그인 요청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final MemberService memberService;

	@Operation(summary = "카카오 로그인 API", description = "카카오 로그인 페이지로 이동 요청한다.(사용불가!!)")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/signin/kakao")
	public ResponseEntity<?> kakaoLoginRedirect() {
		HttpHeaders httpHeaders = new HttpHeaders();
		// 카카오 로그인 페이지로 리다이렉트
		httpHeaders.setLocation(URI.create("/oauth2/authorization/kakao"));
		return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
	}

	@Operation(summary = "임시 로그인/회원가입(토큰 발급) API", description = "로그인 또는 회원가입 후 토큰을 발급한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/token")
	public ResponseEntity<String> getTempToken(@RequestBody @Valid TempLoginRequest request) {
		String accessToken = authService.swaggerToken(request);
		return ResponseEntity.ok(accessToken);

	}

	@Operation(summary = "닉네임 중복 검증 API", description = "닉네임 중복을 검증한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/check-nickname")
	public ResponseEntity<ValidateNickNameResponse> checkNickName(
		@RequestBody @Valid ValidateNickNameRequest request) {
		return ResponseEntity.ok(memberService.isDuplicatedNickname(request));
	}

	@Operation(summary = "추가정보 API", description = "추가 정보를 저장한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/member")
	public ResponseEntity<SignUpResponse> signUp(
		@RequestBody @Valid AdditionalInfoRequest request,
		@AuthenticationPrincipal Member loginMember) {
		SignUpResponse response = memberService.signUp(request, loginMember.getSocialEmail());

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "로그아웃 API", description = "로그아웃한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/logout")
	public ResponseEntity<LogoutResponse> logout(@RequestBody @Valid LogoutRequest request) {
		LogoutResponse response = memberService.logout(request);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "토큰 재발급 API", description = "토큰을 재발급한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/reissue/token")
	public ResponseEntity<ReissueResponse> reissue(@RequestBody @Valid ReissueRequest request) {
		ReissueResponse response = memberService.reissue(request);
		return ResponseEntity.ok(response);
	}
}

