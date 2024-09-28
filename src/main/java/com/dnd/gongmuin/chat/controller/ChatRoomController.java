package com.dnd.gongmuin.chat.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.chat.dto.request.CreateChatRoomRequest;
import com.dnd.gongmuin.chat.dto.response.AcceptChatResponse;
import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chat.dto.response.RejectChatResponse;
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

	@Operation(summary = "채팅방 목록 조회 API", description = "회원의 채팅방 목록을 조회한다.")
	@GetMapping("/api/chat-rooms")
	public ResponseEntity<List<ChatRoomSimpleResponse>> getChatRoomsByMember(
		@RequestParam("status") String status,
		@AuthenticationPrincipal Member member
	) {
		List<ChatRoomSimpleResponse> response = chatRoomService.getChatRoomsByMember(member, status);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅방 조회 API", description = "채팅방 아이디로 채팅방을 조회한다.")
	@GetMapping("/api/chat-rooms/{chatRoomId}")
	public ResponseEntity<ChatRoomDetailResponse> createChatRoom(
		@PathVariable("chatRoomId") Long chatRoomId,
		@AuthenticationPrincipal Member member
	) {
		ChatRoomDetailResponse response = chatRoomService.getChatRoomById(chatRoomId, member);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅 수락 API", description = "채팅방에서 요청자와의 채팅을 수락한다.")
	@PatchMapping("/api/chat-rooms/{chatRoomId}/accept")
	public ResponseEntity<AcceptChatResponse> acceptChat(
		@PathVariable("chatRoomId") Long chatRoomId,
		@AuthenticationPrincipal Member member
	) {
		AcceptChatResponse response = chatRoomService.acceptChat(chatRoomId, member);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅 거절 API", description = "채팅방에서 요청자와의 채팅을 거절한다.")
	@PatchMapping("/api/chat-rooms/{chatRoomId}/reject")
	public ResponseEntity<RejectChatResponse> rejectChat(
		@PathVariable("chatRoomId") Long chatRoomId,
		@AuthenticationPrincipal Member member
	) {
		RejectChatResponse response = chatRoomService.rejectChat(chatRoomId, member);
		return ResponseEntity.ok(response);
	}
}
