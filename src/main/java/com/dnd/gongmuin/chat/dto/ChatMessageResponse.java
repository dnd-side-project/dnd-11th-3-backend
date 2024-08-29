package com.dnd.gongmuin.chat.dto;

public record ChatMessageResponse(
	Long memberId,
	Long chatRoomId,
	String content,
	String type,
	String mediaUrl
) {
}
