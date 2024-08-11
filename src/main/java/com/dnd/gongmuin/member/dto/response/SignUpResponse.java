package com.dnd.gongmuin.member.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = " ResponseDTO")
public record SignUpResponse(

	@Schema(description = "회원가입 요청한 닉네임")
	String nickName
) {
}
