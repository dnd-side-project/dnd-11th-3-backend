package com.dnd.gongmuin.notification.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.dto.request.readNotificationRequest;
import com.dnd.gongmuin.notification.dto.response.NotificationResponse;
import com.dnd.gongmuin.notification.dto.response.readNotificationResponse;
import com.dnd.gongmuin.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "알림 API", description = "알림 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/api/notifications")
	public ResponseEntity<PageResponse<NotificationResponse>> getNotificationsByMember(
		@RequestParam("type") String type,
		@AuthenticationPrincipal Member member,
		Pageable pageable) {

		PageResponse<NotificationResponse> response =
			notificationService.getNotificationsByMember(type, member, pageable);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/api/notification/read")
	public ResponseEntity<readNotificationResponse> readNotification(
		@RequestBody @Valid readNotificationRequest request,
		@AuthenticationPrincipal Member member
	) {
		readNotificationResponse response = notificationService.readNotification(request, member);

		return ResponseEntity.ok(response);
	}
}
