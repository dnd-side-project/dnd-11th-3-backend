package com.dnd.gongmuin.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.member.dto.response.SignUpResponse;
import com.dnd.gongmuin.member.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.member.service.MemberService;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/check-nickname")
	public ResponseEntity<ValidateNickNameResponse> checkNickName(
		@RequestBody ValidateNickNameRequest validateNickNameRequest) {
		return ResponseEntity.ok(memberService.isDuplicatedNickname(validateNickNameRequest));
	}

	@PostMapping("/member")
	public ResponseEntity<SignUpResponse> signUp(@RequestBody AdditionalInfoRequest request,
		@AuthenticationPrincipal CustomOauth2User loginMember) {
		SignUpResponse response = memberService.signUp(request, loginMember.getEmail());

		return ResponseEntity.ok(response);
	}

}