package com.dnd.gongmuin.redis.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisErrorCode implements ErrorCode {

	REDIS_SAVE_ERROR("저장하지 못했습니다.", "REDIS_001"),
	REDIS_FIND_ERROR("값을 찾는 도중 오류가 발생했습니다.", "REDIS_002"),
	REDIS_DELETE_ERROR("삭제하지 못했습니다.", "REDIS_003"),
	REDIS_EXPIRE_ERROR("만료시키지하지 못했습니다.", "REDIS_004"),
	REDIS_EXPIRED_ERROR("만료된 키 입니다.", "REDIS_005");

	private final String message;
	private final String code;
}
