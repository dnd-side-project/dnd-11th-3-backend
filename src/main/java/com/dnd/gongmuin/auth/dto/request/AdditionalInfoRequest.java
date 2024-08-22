package com.dnd.gongmuin.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdditionalInfoRequest(

	@NotBlank(message = "공무원 이메일은 필수 입력 항목입니다.")
	String officialEmail,

	@NotBlank(message = "닉네임은 필수 입력 항목입니다.")
	@Size(min = 2, max = 12, message = "닉네임은 최소 2자리 이상 최대 12자 이하입니다.")
	String nickname,

	@NotBlank(message = "직군은 필수 입력 항목입니다.")
	String jobGroup,

	@NotBlank(message = "직렬은 필수 입력 항목입니다.")
	String jobCategory
) {
}
