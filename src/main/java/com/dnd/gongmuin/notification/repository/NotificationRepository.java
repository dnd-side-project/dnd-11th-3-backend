package com.dnd.gongmuin.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationCustom {
	void deleteByMember(Member member);
}
