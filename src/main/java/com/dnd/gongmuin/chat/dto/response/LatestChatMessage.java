package com.dnd.gongmuin.chat.dto.response;

import java.time.LocalDateTime;

public record LatestChatMessage(
	Long chatRoomId,
	String content,
	String type,
	LocalDateTime createdAt
) {
}
