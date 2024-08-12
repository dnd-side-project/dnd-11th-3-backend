package com.dnd.gongmuin.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

@Tag(name = "Member API", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "닉네임 중복 검증 API", description = "닉네임 중복을 검증한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/check-nickname")
	public ResponseEntity<ValidateNickNameResponse> checkNickName(
		@RequestBody @Valid ValidateNickNameRequest validateNickNameRequest) {
		return ResponseEntity.ok(memberService.isDuplicatedNickname(validateNickNameRequest));
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
