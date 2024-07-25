package com.dnd.gongmuin.member.domain;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MemberTest {

	@DisplayName("소셜 이메일을 변경할 수 있다.")
	@Test
	void test() {
		// given
		Member 공무인1 = createMember("공무인1", "kakao1234/영태", "gongmuin@nate.com", "gongmuin@korea.kr");

		// when
		공무인1.updateSocialEmail("gongmuin2@daum.net");

		// then
		assertThat(공무인1.getSocialEmail()).isEqualTo("gongmuin2@daum.net");

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