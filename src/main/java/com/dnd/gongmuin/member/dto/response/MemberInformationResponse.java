package com.dnd.gongmuin.member.dto.response;

public record MemberInformationResponse(
	Long memberId,
	String nickname,
	String socialName,
	String officialEmail,
	String socialEmail,
	String jobGroup,
	String jobCategory,
	int credit,
	int profileImageNo
) {
}
