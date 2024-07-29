package com.dnd.gongmuin.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.member.dto.request.ValidNickNameRequest;
import com.dnd.gongmuin.member.dto.response.ValidNickNameResponse;
import com.dnd.gongmuin.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@PostMapping("/api/auth/check-nickname")
	public ResponseEntity<ValidNickNameResponse> checkNickName(@RequestBody ValidNickNameRequest validNickNameRequest) {
		return ResponseEntity.ok(memberService.isDuplicatedNickname(validNickNameRequest));
	}

}
