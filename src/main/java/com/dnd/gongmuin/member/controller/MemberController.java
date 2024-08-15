package com.dnd.gongmuin.member.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.UpdateMemberProfileRequest;
import com.dnd.gongmuin.member.dto.response.MemberProfileResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsByMemberResponse;
import com.dnd.gongmuin.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member API", description = "회원 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "프로필 조회 API", description = "로그인 된 사용자 프로필 정보를 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/profile")
	public ResponseEntity<MemberProfileResponse> getMemberProfile(@AuthenticationPrincipal Member member) {
		MemberProfileResponse response = memberService.getMemberProfile(member);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "프로필 수정 API", description = "로그인 된 사용자 프로필 정보를 수정한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PatchMapping("/profile/edit")
	public ResponseEntity<MemberProfileResponse> updateMemberProfile(
		@RequestBody UpdateMemberProfileRequest request,
		@AuthenticationPrincipal Member member) {
		MemberProfileResponse response = memberService.updateMemberProfile(request, member);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "작성한 질문 전체 조회 API", description = "작성한 질문을 전체 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/question-posts")
	public ResponseEntity<PageResponse<QuestionPostsByMemberResponse>> getQuestionPostsByMember(
		@AuthenticationPrincipal Member member,
		Pageable pageable) {
		PageResponse<QuestionPostsByMemberResponse> response =
			memberService.getQuestionPostsByMember(member, pageable);

		return ResponseEntity.ok(response);
	}

}
