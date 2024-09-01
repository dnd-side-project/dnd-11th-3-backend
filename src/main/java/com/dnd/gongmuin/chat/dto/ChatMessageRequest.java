package com.dnd.gongmuin.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
	@NotBlank(message = "채팅 내용을 입력해주세요.")
	String content,
	@NotBlank(message = "채팅 타입을 입력해주세요.")
	String type
) {
}
