package com.dnd.gongmuin.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateChatRoomRequest(
	@NotBlank(message = "질문 게시글 아이디를 입력해주세요.")
	Long questionPostId,

	@NotBlank(message = "답변자 아이디를 입력해주세요.")
	Long answererId
) {
}
