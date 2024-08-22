package com.dnd.gongmuin.member.domain;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.gongmuin.common.fixture.MemberFixture;

class MemberTest {

	@DisplayName("소셜 이메일을 변경할 수 있다.")
	@Test
	void updateSocialEmail() {
		// given
		Member member = MemberFixture.member();

		// when
		member.updateSocialEmail("gongmuin2@daum.net");

		// then
		assertThat(member.getSocialEmail()).isEqualTo("gongmuin2@daum.net");
	}

	@DisplayName("추가 정보를 업데이트 할 수 있다.")
	@Test
	void updateAdditionalInfo() {
		// given
		Member member = MemberFixture.member3();

		// when
		member.updateAdditionalInfo("김회원",
			"abcd@korea.kr",
			JobGroup.ME,
			JobCategory.ME);

		// then
		assertThat(member).extracting("nickname", "officialEmail")
			.containsExactlyInAnyOrder(
				"김회원",
				"abcd@korea.kr"
			);

	}
}