package com.dnd.gongmuin.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateChatRoomRequest(
	@NotNull(message = "질문 게시글 아이디는 필수 입력 항목입니다.")
	Long questionPostId,

	@NotNull(message = "답변자 아이디는 필수 입력 항목입니다.")
	Long answererId
) {
}
