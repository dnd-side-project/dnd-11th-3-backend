package com.dnd.gongmuin.auth.cotroller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	@GetMapping("/signin/kakao")
	public ResponseEntity<?> kakaoLoginRedirect() {
		HttpHeaders httpHeaders = new HttpHeaders();
		// 카카오 로그인 페이지로 리다이렉트
		httpHeaders.setLocation(URI.create("/oauth2/authorization/kakao"));
		return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_PERMANENTLY);
	}
}

