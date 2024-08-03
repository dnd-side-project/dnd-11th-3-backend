package com.dnd.gongmuin.question_post.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.question_post.dto.QuestionPostDetailResponse;
import com.dnd.gongmuin.question_post.dto.RegisterQuestionPostRequest;
import com.dnd.gongmuin.question_post.service.QuestionPostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-posts")
public class QuestionPostController {

	private final QuestionPostService questionPostService;

	@PostMapping
	public ResponseEntity<QuestionPostDetailResponse> registerQuestionPost(
		@RequestBody RegisterQuestionPostRequest request
	) {
		QuestionPostDetailResponse response = questionPostService.registerQuestionPost(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("{/questionPostId}")
	public ResponseEntity<QuestionPostDetailResponse> getQuestionPostById(
		@PathVariable("questionPostId") Long questionPostId
	) {
		QuestionPostDetailResponse response = questionPostService.getQuestionPostById(questionPostId);
		return ResponseEntity.ok(response);
	}
}