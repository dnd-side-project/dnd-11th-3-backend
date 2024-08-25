package com.dnd.gongmuin.post_interaction.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InteractionErrorCode implements ErrorCode {

	NOT_FOUND_INTERACTION("상호작용 이력이 존재하지 않습니다.", "PI_001"),
	NOT_FOUND_INTERACTION_COUNT("상호작용 수 이력이 존재하지 않습니다.", "PI_002"),

	ALREADY_INTERACTED("이미 해당 작업을 수행했습니다.", "PI_003"),
	INTERACTION_UNDO_NOT_ALLOWED("상호작용 수가 0이하가 될 수 없습니다.", "PI_004"),
	INTERACTION_NOT_ALLOWED("본인 게시물은 상호작용할 수 없습니다", "PI_005"),
	NOT_FOUND_TYPE("북마크와 추천 중 하나를 입력해주세요", "PI_006");

	private final String message;
	private final String code;
}
