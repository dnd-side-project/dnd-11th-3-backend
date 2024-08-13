package com.dnd.gongmuin.post_interaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.dto.PostInteractionResponse;
import com.dnd.gongmuin.post_interaction.service.PostInteractionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-posts")
public class PostInteractionController {

	private final PostInteractionService postInteractionService;

	@PostMapping("/{questionPostId}/activated")
	public ResponseEntity<PostInteractionResponse> activateInteraction(
		@PathVariable Long questionPostId,
		@RequestParam String type,
		@AuthenticationPrincipal Member member
	) {
		PostInteractionResponse response = postInteractionService.activateInteraction(
			questionPostId,
			member.getId(),
			InteractionType.from(type)
		);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/{questionPostId}/inactivated")
	public ResponseEntity<PostInteractionResponse> inactivateInteraction(
		@PathVariable("questionPostId") Long questionPostId,
		@RequestParam("type") String type,
		@AuthenticationPrincipal Member member
	) {
		PostInteractionResponse response = postInteractionService.inactivateInteraction(
			questionPostId,
			member.getId(),
			InteractionType.from(type)
		);
		return ResponseEntity.ok(response);
	}
}
