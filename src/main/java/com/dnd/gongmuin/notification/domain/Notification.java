package com.dnd.gongmuin.notification.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private NotificationType type;

	@Column(name = "is_read", nullable = false)
	private Boolean isRead;

	@Column(name = "target_url")
	private String targetUrl;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	@Builder
	public Notification(NotificationType type, Boolean isRead, String targetUrl, Member member) {
		this.type = type;
		this.isRead = isRead;
		this.targetUrl = targetUrl;
		this.member = member;
	}
}
