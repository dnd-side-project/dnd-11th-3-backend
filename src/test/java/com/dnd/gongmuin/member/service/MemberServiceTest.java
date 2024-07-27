package com.dnd.gongmuin.member.service;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;

@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@DisplayName("조합된 소셜 이름 부분 중 공급자 부분을 얻을 수 있다.")
	@Test
	void parseProviderFromSocialName() {
		// given
		Member kakaoMember = createMember("김철수", "kakao123/철수", "kakao123@daum.net", "abc123@korea.com");
		Member naverMember = createMember("김철수", "naver123/철수", "naver123@naver.com", "abc321@korea.com");

		// when
		String kakaoProvider = memberService.parseProviderFromSocialName(kakaoMember);
		String naverProvider = memberService.parseProviderFromSocialName(naverMember);

		// then
		assertThat(kakaoProvider).isEqualToIgnoringCase("kakao");
		assertThat(naverProvider).isEqualToIgnoringCase("naver");
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