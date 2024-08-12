package com.dnd.gongmuin.question_post.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.dto.request.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;
import com.dnd.gongmuin.question_post.service.QuestionPostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "질문글 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-posts")
public class QuestionPostController {

	private final QuestionPostService questionPostService;

	@Operation(summary = "질문글 등록 API", description = "질문글을 등록한다")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping
	public ResponseEntity<QuestionPostDetailResponse> registerQuestionPost(
		@Valid @RequestBody RegisterQuestionPostRequest request,
		@AuthenticationPrincipal Member member
	) {
		QuestionPostDetailResponse response = questionPostService.registerQuestionPost(request, member);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "질문글 상세 조회 API", description = "질문글을 아이디로 상세조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/{questionPostId}")
	public ResponseEntity<QuestionPostDetailResponse> getQuestionPostById(
		@PathVariable("questionPostId") Long questionPostId
	) {
		QuestionPostDetailResponse response = questionPostService.getQuestionPostById(questionPostId);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "질문글 검색 API", description = "질문글을 키워드로 검색하고 정렬, 필터링을 한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/search")
	public ResponseEntity<PageResponse<QuestionPostSimpleResponse>> searchQuestionPost(
		@ModelAttribute QuestionPostSearchCondition condition,
		Pageable pageable
	) {
		PageResponse<QuestionPostSimpleResponse> response = questionPostService.searchQuestionPost(
			condition, pageable);
		return ResponseEntity.ok(response);
	}
}