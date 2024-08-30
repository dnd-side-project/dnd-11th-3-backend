package com.dnd.gongmuin.post_interaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.dto.InteractionResponse;
import com.dnd.gongmuin.post_interaction.service.InteractionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "상호작용 API")
@RestController
@RequiredArgsConstructor
public class InteractionController {

	private final InteractionService interactionService;

	@Operation(summary = "상호작용 등록 API", description = "게시글을 추천하거나 북마크한다.")
	@PostMapping("/api/question-posts/{questionPostId}/activated")
	public ResponseEntity<InteractionResponse> activateInteraction(
		@PathVariable("questionPostId") Long questionPostId,
		@RequestParam String type,
		@AuthenticationPrincipal Member member
	) {
		InteractionResponse response = interactionService.activateInteraction(
			questionPostId,
			member.getId(),
			InteractionType.from(type)
		);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "상호작용 취소 API", description = "게시글을 추천하거나 북마크 취소한다.")
	@PostMapping("/api/question-posts/{questionPostId}/inactivated")
	public ResponseEntity<InteractionResponse> inactivateInteraction(
		@PathVariable("questionPostId") Long questionPostId,
		@RequestParam("type") String type,
		@AuthenticationPrincipal Member member
	) {
		InteractionResponse response = interactionService.inactivateInteraction(
			questionPostId,
			member.getId(),
			InteractionType.from(type)
		);
		return ResponseEntity.ok(response);
	}
}
