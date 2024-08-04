package com.dnd.gongmuin.common.fixture;

import com.dnd.gongmuin.member.domain.JobCategory;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberFixture {

	public static Member member() {
		return Member.of(
			"김회원",
			"회원123",
			JobGroup.ENGINEERING,
			JobCategory.GAS,
			"KAKAO123/gongmuin@daum.net",
			"gongmuin@korea.kr",
			10000
		);
	}
}
