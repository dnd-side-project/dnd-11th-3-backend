package com.dnd.gongmuin.notification.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.domain.NotificationType;
import com.dnd.gongmuin.notification.dto.response.NotificationsResponse;
import com.dnd.gongmuin.notification.exception.NotificationErrorCode;
import com.dnd.gongmuin.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	@Transactional
	public void saveNotificationFromTarget(
		String targetType,
		Long targetId,
		Long triggerMemberId,
		Member toMember) {
		NotificationType type = findTargetType(targetType);
		Notification notification = Notification.of(type, targetId, triggerMemberId, toMember);
		try {
			notificationRepository.save(notification);
		} catch (Exception e) {
			throw new ValidationException(NotificationErrorCode.SAVE_NOTIFICATION_FAILED);
		}
	}

	private NotificationType findTargetType(String targetType) {
		return NotificationType.of(targetType);
	}

	public PageResponse<NotificationsResponse> getNotificationsByMember(
		String type,
		Member member,
		Pageable pageable) {

		try {
			Slice<NotificationsResponse> responsePage =
				notificationRepository.getNotificationsByMember(type, member, pageable);

			return PageMapper.toPageResponse(responsePage);
		} catch (Exception e) {
			throw new NotFoundException(NotificationErrorCode.NOTIFICATIONS_BY_MEMBER_FAILED);
		}
	}
}
