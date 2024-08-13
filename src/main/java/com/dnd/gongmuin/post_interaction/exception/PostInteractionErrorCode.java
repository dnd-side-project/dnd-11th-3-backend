package com.dnd.gongmuin.post_interaction.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostInteractionErrorCode implements ErrorCode {

	NOT_FOUND_POST_INTERACTION("질문 게시글이 존재하지 않습니다.", "PI_001"),
	ALREADY_RECOMMENDED("이미 추천한 게시글입니다.", "PI_002"),
	ALREADY_UNRECOMMENDED("이미 추천 취소한 게시글입니다.", "PI_003");

	private final String message;
	private final String code;
}
