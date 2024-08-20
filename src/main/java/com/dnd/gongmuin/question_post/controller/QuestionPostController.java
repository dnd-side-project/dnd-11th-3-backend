package com.dnd.gongmuin.question_post.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.request.UpdateQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;
import com.dnd.gongmuin.question_post.dto.response.RecQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.RegisterQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.UpdateQuestionPostResponse;
import com.dnd.gongmuin.question_post.service.QuestionPostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "질문글 API")
@RestController
@RequiredArgsConstructor
public class QuestionPostController {

	private final QuestionPostService questionPostService;

	@Operation(summary = "질문글 등록 API", description = "질문글을 등록한다")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/api/question-posts")
	public ResponseEntity<RegisterQuestionPostResponse> registerQuestionPost(
		@Valid @RequestBody RegisterQuestionPostRequest request,
		@AuthenticationPrincipal Member member
	) {
		RegisterQuestionPostResponse response = questionPostService.registerQuestionPost(request, member);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "질문글 상세 조회 API", description = "질문글을 아이디로 상세조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/api/question-posts/{questionPostId}")
	public ResponseEntity<QuestionPostDetailResponse> getQuestionPostById(
		@PathVariable("questionPostId") Long questionPostId
	) {
		QuestionPostDetailResponse response = questionPostService.getQuestionPostById(questionPostId);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "질문글 검색 API", description = "질문글을 키워드로 검색하고 정렬, 필터링을 한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/api/question-posts/search")
	public ResponseEntity<PageResponse<QuestionPostSimpleResponse>> searchQuestionPost(
		@Valid @ModelAttribute QuestionPostSearchCondition condition,
		Pageable pageable
	) {
		PageResponse<QuestionPostSimpleResponse> response = questionPostService.searchQuestionPost(
			condition, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "질문글 추천 API", description = "직군에 맞는 질문글을 추천순으로 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/api/question-posts/recommends")
	public ResponseEntity<PageResponse<RecQuestionPostResponse>> getRecommendQuestionPosts(
		@AuthenticationPrincipal Member member,
		Pageable pageable
	) {
		PageResponse<RecQuestionPostResponse> response
			= questionPostService.getRecommendQuestionPosts(member, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "질문글 수정 API", description = "질문자가 질문글을 수정한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PatchMapping("/api/question-posts/{questionPostId}/edit")
	public ResponseEntity<UpdateQuestionPostResponse> updateQuestionPosts(
		@PathVariable("questionPostId") Long questionPostId,
		@Valid @RequestBody UpdateQuestionPostRequest request
	) {
		UpdateQuestionPostResponse response
			= questionPostService.updateQuestionPost(questionPostId, request);
		return ResponseEntity.ok(response);
	}
}