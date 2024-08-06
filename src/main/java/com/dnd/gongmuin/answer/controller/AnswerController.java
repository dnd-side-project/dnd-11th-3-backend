package com.dnd.gongmuin.answer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.answer.dto.AnswerDetailResponse;
import com.dnd.gongmuin.answer.dto.RegisterAnswerRequest;
import com.dnd.gongmuin.answer.service.AnswerService;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-posts")
public class AnswerController {
	private final AnswerService answerService;

	@PostMapping("/{questionPostId}/answers")
	public ResponseEntity<AnswerDetailResponse> registerAnswer(
		@PathVariable Long questionPostId,
		@RequestBody RegisterAnswerRequest request,
		@AuthenticationPrincipal Member member
	) {
		AnswerDetailResponse response = answerService.registerAnswer(questionPostId, request, member);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{questionPostId}/answers")
	public ResponseEntity<PageResponse<AnswerDetailResponse>> getAnswersByQuestionPostId(
		@PathVariable Long questionPostId
	) {
		PageResponse<AnswerDetailResponse> response = answerService.getAnswersByQuestionPostId(questionPostId);
		return ResponseEntity.ok(response);
	}
}
