package com.dnd.gongmuin.common.fixture;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

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

	public static Member member(Long id) {
		Member member = Member.of(
			"김회원",
			"회원123",
			JobGroup.ENGINEERING,
			JobCategory.GAS,
			"KAKAO123/gongmuin@daum.net",
			"gongmuin@korea.kr",
			10000
		);

		ReflectionTestUtils.setField(member, "id", id);
		ReflectionTestUtils.setField(member, "createdAt", LocalDateTime.now());

		return member;
	}
}
