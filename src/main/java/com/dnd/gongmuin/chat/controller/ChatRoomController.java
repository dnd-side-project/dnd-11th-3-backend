package com.dnd.gongmuin.chat.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.chat.dto.ChatMessageResponse;
import com.dnd.gongmuin.chat.service.ChatRoomService;
import com.dnd.gongmuin.common.dto.PageResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
}
