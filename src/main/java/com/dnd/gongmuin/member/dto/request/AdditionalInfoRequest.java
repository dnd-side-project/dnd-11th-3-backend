package com.dnd.gongmuin.member.dto.request;

public record AdditionalInfoRequest(
	String officialEmail,
	String nickname,
	String jobGroup,
	String jobCategory
) {
}
