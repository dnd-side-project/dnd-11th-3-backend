package com.dnd.gongmuin.chatroom.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.chatroom.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chatroom.service.ChatRoomService;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;

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

	@Operation(summary = "채팅방 목록 조회 API", description = "회원의 채팅방 목록을 조회한다.")
	@GetMapping("/api/chat-rooms")
	public ResponseEntity<PageResponse<ChatRoomSimpleResponse>> getChatRoomsByMember(
		@AuthenticationPrincipal Member member,
		Pageable pageable
	) {
		PageResponse<ChatRoomSimpleResponse> response
			= chatRoomService.getChatRoomsByMember(member, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅방 상세조회 API", description = "채팅방 아이디로 채팅방을 조회한다.")
	@GetMapping("/api/chat-rooms/{chatRoomId}")
	public ResponseEntity<ChatRoomDetailResponse> getChatRoomById(
		@PathVariable("chatRoomId") Long chatRoomId,
		@AuthenticationPrincipal Member member
	) {
		ChatRoomDetailResponse response = chatRoomService.getChatRoomById(chatRoomId, member);
		return ResponseEntity.ok(response);
	}

}