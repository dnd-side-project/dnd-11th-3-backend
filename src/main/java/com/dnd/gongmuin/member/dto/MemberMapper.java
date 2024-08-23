package com.dnd.gongmuin.member.dto;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.MemberInformationResponse;
import com.dnd.gongmuin.member.dto.response.MemberProfileResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

	public static MemberProfileResponse toMemberProfileResponse(Member member) {
		return new MemberProfileResponse(
			member.getId(),
			member.getNickname(),
			member.getJobGroup().getLabel(),
			member.getJobCategory().getLabel(),
			member.getCredit(),
			member.getProfileImageNo()
		);
	}

	public static MemberInformationResponse toMemberInformationResponse(Member member) {
		return new MemberInformationResponse(
			member.getId(),
			member.getNickname(),
			member.getSocialName(),
			member.getOfficialEmail(),
			member.getSocialEmail(),
			member.getJobGroup().getLabel(),
			member.getJobCategory().getLabel(),
			member.getCredit(),
			member.getProfileImageNo()
		);
	}
}
