package com.dnd.gongmuin.member.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.UpdateMemberProfileRequest;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsResponse;
import com.dnd.gongmuin.member.dto.response.BookmarksResponse;
import com.dnd.gongmuin.member.dto.response.CreditHistoryResponse;
import com.dnd.gongmuin.member.dto.response.MemberInformationResponse;
import com.dnd.gongmuin.member.dto.response.MemberProfileResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsResponse;
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
	public ResponseEntity<PageResponse<QuestionPostsResponse>> getQuestionPostsByMember(
		@AuthenticationPrincipal Member member,
		Pageable pageable) {
		PageResponse<QuestionPostsResponse> response =
			memberService.getQuestionPostsByMember(member, pageable);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "댓글 단 질문 전체 조회 API", description = "댓글 단 질문을 전체 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/question-posts/answers")
	public ResponseEntity<PageResponse<AnsweredQuestionPostsResponse>> getAnsweredQuestionPostsByMember(
		@AuthenticationPrincipal Member member,
		Pageable pageable) {
		PageResponse<AnsweredQuestionPostsResponse> response =
			memberService.getAnsweredQuestionPostsByMember(member, pageable);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "스크랩 질문 전체 조회 API", description = "스크랩한 질문을 전체 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/question-posts/bookmarks")
	public ResponseEntity<PageResponse<BookmarksResponse>> getBookmarksByMember(
		@AuthenticationPrincipal Member member,
		Pageable pageable) {
		PageResponse<BookmarksResponse> response =
			memberService.getBookmarksByMember(member, pageable);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "크레딧 목록 전체 조회 API", description = "타입에 맞는 크레딧 목록을 전체 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/credit/histories")
	public ResponseEntity<PageResponse<CreditHistoryResponse>> getCreditHistoryByMember(
		@RequestParam("type") String type,
		@AuthenticationPrincipal Member member,
		Pageable pageable) {
		PageResponse<CreditHistoryResponse> response =
			memberService.getCreditHistoryByMember(type, member, pageable);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "회원 정보 전체 조회 API", description = "회원 정보를 전체 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/information")
	public ResponseEntity<MemberInformationResponse> getMemberInformation(@AuthenticationPrincipal Member member) {
		MemberInformationResponse response = memberService.getMemberInformation(member);

		return ResponseEntity.ok(response);
	}
}
