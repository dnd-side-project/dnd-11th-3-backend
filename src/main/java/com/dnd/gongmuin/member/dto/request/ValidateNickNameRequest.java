package com.dnd.gongmuin.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "닉네임 중복 검증 RequestDTO")
public record ValidateNickNameRequest(

	@NotBlank(message = "닉네임은 필수 입력 항목입니다.")
	@Size(min = 2, max = 12, message = "닉네임은 최소 2자리 이상 최대 12자 이하입니다.")
	@Schema(description = "중복 검증할 닉네임")
	String nickname
) {

}
