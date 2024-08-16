package com.dnd.gongmuin.post_interaction.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteractionErrorCode implements ErrorCode {

	NOT_FOUND_POST_INTERACTION("상호작용 이력이 존재하지 않습니다.", "PI_001"),
	ALREADY_INTERACTED("이미 해당 작업을 수행했습니다.", "PI_002"),
	INTERACTION_NOT_ALLOWED("본인 게시물은 상호작용할 수 없습니다", "PI_004");

	private final String message;
	private final String code;
}
