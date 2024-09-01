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
import com.dnd.gongmuin.notification.dto.request.IsReadNotificationRequest;
import com.dnd.gongmuin.notification.dto.response.IsReadNotificationResponse;
import com.dnd.gongmuin.notification.dto.response.NotificationsResponse;
import com.dnd.gongmuin.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "알림 API", description = "알림 API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/api/notifications")
	public ResponseEntity<PageResponse<NotificationsResponse>> getNotificationsByMember(
		@RequestParam("type") String type,
		@AuthenticationPrincipal Member member,
		Pageable pageable) {

		PageResponse<NotificationsResponse> response =
			notificationService.getNotificationsByMember(type, member, pageable);

		return ResponseEntity.ok(response);
	}

	@PatchMapping("/api/notification/read")
	public ResponseEntity<IsReadNotificationResponse> isReadNotification(
		@RequestBody IsReadNotificationRequest request
	) {
		IsReadNotificationResponse response = notificationService.isReadNotification(request);

		return ResponseEntity.ok(response);
	}
}
