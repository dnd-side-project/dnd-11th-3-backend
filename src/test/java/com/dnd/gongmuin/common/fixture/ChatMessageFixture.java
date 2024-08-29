package com.dnd.gongmuin.common.fixture;

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
			null,
			MessageType.TEXT
		);
	}
}
