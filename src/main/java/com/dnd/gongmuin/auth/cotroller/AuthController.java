package com.dnd.gongmuin.auth.cotroller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.auth.dto.LoginRequest;
import com.dnd.gongmuin.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Social Login API", description = "소셜 로그인 요청 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

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
	public ResponseEntity<String> swaggerToken(@RequestBody LoginRequest loginRequest) {
		String accessToken = authService.swaggerToken(loginRequest);
		return ResponseEntity.ok(accessToken);

	}
}

