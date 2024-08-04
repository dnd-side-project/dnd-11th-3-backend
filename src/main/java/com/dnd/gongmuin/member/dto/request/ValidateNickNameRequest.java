package com.dnd.gongmuin.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ValidateNickNameRequest(
	@NotBlank(message = "닉네임은 필수 입력 항목입니다.")
	@Size(min = 2, max = 12, message = "닉네임은 최소 2자리 이상 최대 12자 이하입니다.")
	String nickname
) {

}
