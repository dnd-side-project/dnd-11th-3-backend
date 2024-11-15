package com.dnd.gongmuin.chat_inquiry.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.chat_inquiry.dto.AcceptChatResponse;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryRequest;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.RejectChatResponse;
import com.dnd.gongmuin.chat_inquiry.service.ChatInquiryService;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "채팅 요청 API")
@RestController
@RequiredArgsConstructor
public class ChatInquiryController {

	private final ChatInquiryService chatInquiryService;

	@Operation(summary = "채팅 요청 API", description = "답변자 아이디로 채팅 요청을 생성한다.")
	@GetMapping("/api/chat/inquiries")
	public ResponseEntity<CreateChatInquiryResponse> getChatProposalsByMember(
		CreateChatInquiryRequest request,
		@AuthenticationPrincipal Member member
	) {
		CreateChatInquiryResponse response
			= chatInquiryService.createChatInquiry(request, member);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅방 요청 목록 조회 API", description = "회원의 채팅방 목록을 조회한다.")
	@GetMapping("/api/chat/inquires")
	public ResponseEntity<PageResponse<ChatInquiryResponse>> getChatProposalsByMember(
		@AuthenticationPrincipal Member member,
		Pageable pageable
	) {
		PageResponse<ChatInquiryResponse> response
			= chatInquiryService.getChatInquiresByMember(member, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅 수락 API", description = "채팅방에서 요청자와의 채팅을 수락한다.")
	@PatchMapping("/api/chat/inquires/{chatInquiryId}/accept")
	public ResponseEntity<AcceptChatResponse> acceptChat(
		@PathVariable("chatInquiryId") Long chatInquiryId,
		@AuthenticationPrincipal Member member
	) {
		AcceptChatResponse response = chatInquiryService.acceptChat(chatInquiryId, member);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "채팅 거절 API", description = "채팅방에서 요청자와의 채팅을 거절한다.")
	@PatchMapping("/api/chat/inquires/{chatInquiryId}/reject")
	public ResponseEntity<RejectChatResponse> rejectChat(
		@PathVariable("chatInquiryId") Long chatInquiryId,
		@AuthenticationPrincipal Member member
	) {
		RejectChatResponse response = chatInquiryService.rejectChat(chatInquiryId, member);
		return ResponseEntity.ok(response);
	}
}
