package com.dnd.gongmuin.notification.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.dto.response.NotificationResponse;

public interface NotificationCustom {

	Slice<NotificationResponse> getNotificationsByMember(String type, Member member, Pageable pageable);

}
