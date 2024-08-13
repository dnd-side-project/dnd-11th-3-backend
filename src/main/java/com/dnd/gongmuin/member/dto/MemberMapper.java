package com.dnd.gongmuin.member.dto;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.MemberProfileResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberMapper {

	public static MemberProfileResponse toMemberProfileResponse(Member member) {
		return new MemberProfileResponse(
			member.getNickname(),
			member.getJobGroup().getLabel(),
			member.getJobCategory().getLabel(),
			member.getCredit()
		);
	}
}
