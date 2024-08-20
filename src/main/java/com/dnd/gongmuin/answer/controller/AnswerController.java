package com.dnd.gongmuin.answer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.answer.dto.AnswerDetailResponse;
import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.service.AnswerService;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "답변 API")
@RestController
@RequiredArgsConstructor
public class AnswerController {
	private final AnswerService answerService;

	@Operation(summary = "답변 등록 API", description = "질문글에 대한 답변을 작성한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/api/question-posts/{questionPostId}/answers")
	public ResponseEntity<AnswerDetailResponse> registerAnswer(
		@PathVariable Long questionPostId,
		@Valid @RequestBody RegisterAnswerRequest request,
		@AuthenticationPrincipal Member member
	) {
		AnswerDetailResponse response = answerService.registerAnswer(questionPostId, request, member);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "답변 조회 API", description = "질문글에 속하는 답변을 모두 조회한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@GetMapping("/api/question-posts/{questionPostId}/answers")
	public ResponseEntity<PageResponse<AnswerDetailResponse>> getAnswersByQuestionPostId(
		@PathVariable Long questionPostId
	) {
		PageResponse<AnswerDetailResponse> response = answerService.getAnswersByQuestionPostId(questionPostId);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "답변 채택 API", description = "질문자가 답변을 채택한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/api/question-posts/answers/{answerId}")
	public ResponseEntity<AnswerDetailResponse> getAnswersByQuestionPostId(
		@PathVariable Long answerId,
		@AuthenticationPrincipal Member member
	) {
		AnswerDetailResponse response = answerService.chooseAnswer(answerId, member);
		return ResponseEntity.ok(response);
	}
}
