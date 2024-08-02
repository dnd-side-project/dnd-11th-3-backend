package com.dnd.gongmuin.member.service;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.member.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.member.repository.MemberRepository;

@Transactional
@SpringBootTest
class MemberServiceTest {

	@Autowired
	MemberService memberService;

	@Autowired
	MemberRepository memberRepository;

	@DisplayName("조합된 소셜 이메일 부분 중 공급자 부분을 얻을 수 있다.")
	@Test
	void parseProviderFromSocialEmail() {
		// given
		Member kakaoMember = createMember("김철수", "철수", "kakao123/kakao123@daum.net", "abc123@korea.com");
		Member naverMember = createMember("김철수", "철수", "naver123/naver123@naver.com", "abc321@korea.com");

		// when
		String kakaoProvider = memberService.parseProviderFromSocialEmail(kakaoMember);
		String naverProvider = memberService.parseProviderFromSocialEmail(naverMember);

		// then
		assertThat(kakaoProvider).isEqualToIgnoringCase("kakao");
		assertThat(naverProvider).isEqualToIgnoringCase("naver");
	}

	@DisplayName("공무원 이메일이 존재하는지 체크한다.")
	@Test
	void isOfficialEmail() {
		// given
		Member kakaoMember = createMember("김철수", "철수", "kakao123/kakao123@daum.net", "abc123@korea.com");

		// when
		boolean result = memberService.isOfficialEmail(kakaoMember);

		// then
		assertThat(result).isFalse();

	}

	@DisplayName("중복 닉네임이 존재하는지 체크한다.")
	@Test
	void isDuplicatedNickname() {
		// given
		Member member1 = createMember("김철수", "철수", "kakao123/kakao123@daum.net", "abc123@korea.com");
		memberRepository.save(member1);

		ValidateNickNameRequest request = new ValidateNickNameRequest("김철수");

		// when
		ValidateNickNameResponse duplicatedNickname = memberService.isDuplicatedNickname(request);

		// then
		assertThat(duplicatedNickname.isDuplicated()).isTrue();
	}

	@DisplayName("신규 회원은 추가 정보가 업데이트 된다.")
	@Test
	void signUp() {
		// given
		Member member1 = createMember(null, "철수", "kakao123/kakao123@daum.net", null);
		memberRepository.save(member1);

		AdditionalInfoRequest request = new AdditionalInfoRequest("abc123@korea.com", "김신규", "공업", "가스");

		// when
		memberService.signUp(request, "kakao123/kakao123@daum.net");

		// then
		assertThat(member1).extracting("officialEmail", "nickname", "jobGroup", "jobCategory")
			.containsExactlyInAnyOrder(
				"abc123@korea.com",
				"김신규",
				GAS,
				ENGINEERING
			);

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