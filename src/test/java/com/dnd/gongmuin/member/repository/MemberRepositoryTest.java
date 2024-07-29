package com.dnd.gongmuin.member.repository;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.member.domain.Member;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@DisplayName("소셜이메일로 특정 회원을 조회한다.")
	@Test
	void test() {
		// given
		Member 공무인1 = createMember("공무인1", "영태", "kakao1234/gongmuin@nate.com", "gongumin@korea.kr");
		Member savedMember = memberRepository.save(공무인1);

		// when
		Member findMember = memberRepository.findBySocialEmail("kakao1234/gongmuin@nate.com").get();

		// then
		assertThat(findMember.getNickname()).isEqualTo("공무인1");
	}

	private Member createMember(String nickname, String socialName, String socialEmail, String officialEmail) {
		return Member.builder()
			.nickname(nickname)
			.socialName(socialName)
			.socialEmail(socialEmail)
			.officialEmail(officialEmail)
			.jobCategory(GAS)
			.jobGroup(ENGINEERING)
			.credit(10000)
			.build();

	}

}