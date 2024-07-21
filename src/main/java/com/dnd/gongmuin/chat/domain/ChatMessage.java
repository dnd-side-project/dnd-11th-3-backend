package com.dnd.gongmuin.chat.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ChatMessage extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "chat_message_id")
	private Long id;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "is_read", nullable = false)
	private Boolean isRead;

	@Column(name = "media_url", nullable = false)
	private String mediaUrl;

	@Enumerated(STRING)
	@Column(name = "type", nullable = false)
	private MessageType type;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "chat_room_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private ChatRoom chatRoom;
}
