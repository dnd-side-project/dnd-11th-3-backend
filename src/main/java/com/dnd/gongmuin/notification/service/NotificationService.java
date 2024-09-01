package com.dnd.gongmuin.notification.service;

import org.springframework.stereotype.Service;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.domain.NotificationType;
import com.dnd.gongmuin.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	public void saveNotificationFromTarget(String targetType, Long targetId, Member toMember) {
		NotificationType type = findTargetType(targetType);
		Notification notification = Notification.of(type, targetId, toMember);

		notificationRepository.save(notification);
	}

	private NotificationType findTargetType(String targetType) {
		return NotificationType.of(targetType);
	}
}
