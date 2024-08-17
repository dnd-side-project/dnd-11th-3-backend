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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InteractionController {

	private final InteractionService interactionService;

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
