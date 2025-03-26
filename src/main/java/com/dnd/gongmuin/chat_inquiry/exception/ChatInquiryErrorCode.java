package com.dnd.gongmuin.chat_inquiry.exception;

import com.dnd.gongmuin.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatInquiryErrorCode implements ErrorCode {

	NOT_FOUND_INQUIRY("해당 아이디의 채팅 요청이 존재하지 않습니다.", "CI_001"),
	UNAUTHORIZED_REQUEST("채팅 요청을 수락을 하거나 거절할 권한이 없습니다.", "CI_002"),
	UNABLE_TO_CHANGE_STATUS("이미 수락했거나 거절한 요청입니다.", "CI_003"),
	NOT_FOUND_STATUS("채팅방 상태값을 올바르게 입력해주세요.", "CI_004"),
	NOT_EXISTS_ANSWERER("해당 아이디의 답변자가 해당 게시글에 존재하지 않습니다.", "CI_005"),
	SELF_INQUIRY_NOT_ALLOWED("자기 자신에게 채팅 요청을 보낼 수 없습니다.", "CI_006"),
	ALREADY_REQUESTED("동일한 질문글, 유저에 대한 채팅 요청 이력이 존재합니다.", "CI_007");

	private final String message;
	private final String code;
}
