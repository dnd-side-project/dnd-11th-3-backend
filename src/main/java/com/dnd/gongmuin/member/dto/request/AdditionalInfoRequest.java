package com.dnd.gongmuin.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 RequestDTO")
public record AdditionalInfoRequest(

	@NotBlank(message = "공무원 이메일은 필수 입력 항목입니다.")
	@Schema(description = "중복 및 인증 코드가 검증된 공무원 이메일")
	String officialEmail,

	@NotBlank(message = "닉네임은 필수 입력 항목입니다.")
	@Size(min = 2, max = 12, message = "닉네임은 최소 2자리 이상 최대 12자 이하입니다.")
	@Schema(description = "중복 검증 된 닉네임")
	String nickname,

	@NotBlank(message = "직군은 필수 입력 항목입니다.")
	@Schema(description = "직군")
	String jobGroup,

	@NotBlank(message = "직렬은 필수 입력 항목입니다.")
	@Schema(description = "직렬")
	String jobCategory
) {
}
