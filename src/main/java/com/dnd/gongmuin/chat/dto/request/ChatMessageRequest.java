package com.dnd.gongmuin.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatMessageRequest(
	@NotBlank(message = "채팅 내용을 입력해주세요.")
	String content,
	@NotBlank(message = "채팅 타입을 입력해주세요.")
	String type,

	@NotNull(message = "회원 아이디를 입력해주세요.")
	Long memberId
) {
}
