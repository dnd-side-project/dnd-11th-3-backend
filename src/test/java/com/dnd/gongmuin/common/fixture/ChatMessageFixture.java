package com.dnd.gongmuin.common.fixture;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.domain.MessageType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageFixture {

	public static ChatMessage chatMessage() {
		return ChatMessage.of(
			"하하",
			1L,
			1L,
			MessageType.TEXT
		);
	}

	public static ChatMessage chatMessage(Long chatRoomId, String content, LocalDateTime createdAt) {
		ChatMessage chatmessage = ChatMessage.of(
			content,
			chatRoomId,
			1L,
			MessageType.TEXT
		);
		ReflectionTestUtils.setField(chatmessage, "createdAt", createdAt);
		return chatmessage;
	}
}
