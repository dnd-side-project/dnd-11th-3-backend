package com.dnd.gongmuin.chat.domain;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "chat_messages")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ChatMessage {

	@Id
	private String id;

	private String content;

	private long chatRoomId;

	private long memberId;

	private Boolean isRead;

	private MessageType type;

	private LocalDateTime createdAt;

	private ChatMessage(
		String content,
		long chatRoomId,
		long memberId,
		MessageType type
	) {
		this.content = content;
		this.chatRoomId = chatRoomId;
		this.memberId = memberId;
		this.type = type;
		this.isRead = false;
		this.createdAt = LocalDateTime.now();
	}

	public static ChatMessage of(
		String content,
		long chatRoomId,
		long memberId,
		MessageType type
	) {
		return new ChatMessage(content, chatRoomId, memberId, type);
	}
}
