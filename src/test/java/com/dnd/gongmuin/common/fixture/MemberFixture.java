package com.dnd.gongmuin.common.fixture;

import static lombok.AccessLevel.*;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class MemberFixture {

	public static Member member(){
		return Member.builder()
			.nickname("test")
			.jobGroup(JobGroup.of("공업"))
			.build();
	}
}
