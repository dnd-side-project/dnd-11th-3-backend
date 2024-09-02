package com.dnd.gongmuin.notification.service;

import java.util.Objects;

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
import com.dnd.gongmuin.notification.dto.NotificationMapper;
import com.dnd.gongmuin.notification.dto.request.readNotificationRequest;
import com.dnd.gongmuin.notification.dto.response.NotificationsResponse;
import com.dnd.gongmuin.notification.dto.response.readNotificationResponse;
import com.dnd.gongmuin.notification.exception.NotificationErrorCode;
import com.dnd.gongmuin.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	@Transactional
	public void saveNotificationFromTarget(
		NotificationType type,
		Long targetId,
		Long triggerMemberId,
		Member toMember) {
		Notification notification = Notification.of(type, targetId, triggerMemberId, toMember);
		try {
			notificationRepository.save(notification);
		} catch (Exception e) {
			throw new ValidationException(NotificationErrorCode.SAVE_NOTIFICATION_FAILED);
		}
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

	@Transactional
	public readNotificationResponse readNotification(readNotificationRequest request, Member member) {
		Notification findNotification = notificationRepository.findById(request.notificationId())
			.orElseThrow(() -> new NotFoundException(NotificationErrorCode.NOT_FOUND_NOTIFICATION));

		if (!isNotificationOwnedByMember(findNotification, member)) {
			throw new ValidationException(NotificationErrorCode.INVALID_NOTIFICATION_OWNER);
		}

		if (Boolean.TRUE.equals(findNotification.getIsRead())) {
			throw new ValidationException(NotificationErrorCode.CHANGE_IS_READ_NOTIFICATION_FAILED);
		}

		findNotification.updateIsReadTrue();

		return NotificationMapper.toIsReadNotificationResponse(findNotification);
	}

	private boolean isNotificationOwnedByMember(Notification notification, Member member) {
		return Objects.equals(notification.getMember().getId(), member.getId());
	}
}
