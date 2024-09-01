package com.dnd.gongmuin.notification.repository;

import static com.dnd.gongmuin.notification.domain.QNotification.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.domain.QMember;
import com.dnd.gongmuin.notification.domain.NotificationType;
import com.dnd.gongmuin.notification.domain.QNotification;
import com.dnd.gongmuin.notification.dto.response.NotificationsResponse;
import com.dnd.gongmuin.notification.dto.response.QNotificationsResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotificationCustomImpl implements NotificationCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<NotificationsResponse> getNotificationsByMember(
		String type,
		Member member,
		Pageable pageable) {
		QNotification nc = notification;
		QMember tm = QMember.member;

		List<NotificationsResponse> content = queryFactory
			.select(new QNotificationsResponse(
				nc,
				tm
			))
			.from(nc)
			.join(tm).on(nc.triggerMemberId.eq(tm.id))
			.where(
				nc.member.eq(member),
				targetTypeEq(type)
			)
			.orderBy(nc.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1L)
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	private BooleanExpression targetTypeEq(String type) {
		if (type == null || type.isEmpty() || "전체".equals(type)) {
			return null;
		}

		return notification.type.in(NotificationType.of(type));
	}

	private <T> boolean hasNext(int pageSize, List<T> content) {
		if (content.size() <= pageSize) {
			return false;
		}
		content.remove(pageSize);
		return true;
	}
}

