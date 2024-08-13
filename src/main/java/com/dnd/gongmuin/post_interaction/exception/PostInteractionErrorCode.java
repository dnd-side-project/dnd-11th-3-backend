package com.dnd.gongmuin.post_interaction.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostInteractionErrorCode implements ErrorCode {

	NOT_FOUND_POST_INTERACTION("질문 게시글이 존재하지 않습니다.", "PI_001"),
	ALREADY_INTERACTED("이미 상호작용한 게시글입니다.", "PI_002"),
	ALREADY_UNINTERACTED("이미 상호작용 취소한 게시글입니다.", "PI_003"),
	INTERACTION_NOT_ALLOWED("본인 게시물은 상호작용할 수 없습니다", "PI_004");


	private final String message;
	private final String code;
}
