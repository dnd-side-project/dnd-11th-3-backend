package com.dnd.gongmuin.notification.service;

import org.springframework.stereotype.Service;

import com.dnd.gongmuin.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
}
