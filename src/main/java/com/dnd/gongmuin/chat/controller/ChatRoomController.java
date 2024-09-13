package com.dnd.gongmuin.chat.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chat.dto.request.CreateChatRoomRequest;
import com.dnd.gongmuin.chat.service.ChatRoomService;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "채팅방 API")
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	@Operation(summary = "채팅방 메시지 조회 API", description = "채팅방 메시지를 최신순으로 페이징한다.")
	@GetMapping("/api/chat-messages/{chatRoomId}")
	public ResponseEntity<PageResponse<ChatMessageResponse>> getChatMessages(
		@PathVariable("chatRoomId") Long chatRoomId,
		Pageable pageable
	) {
		PageResponse<ChatMessageResponse> response =
			chatRoomService.getChatMessages(chatRoomId, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅방 생성 API", description = "요청자가 답변자와의 채팅방을 생성한다.")
	@PostMapping("/api/chat-rooms")
	public ResponseEntity<ChatRoomDetailResponse> createChatRoom(
		@Valid @RequestBody CreateChatRoomRequest request,
		@AuthenticationPrincipal Member member
	) {
		ChatRoomDetailResponse response = chatRoomService.createChatRoom(request, member);
		return ResponseEntity.ok(response);
	}
}
