package com.dnd.gongmuin.member.service;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.member.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	MemberRepository memberRepository;

	@InjectMocks
	MemberService memberService;

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
		given(memberRepository.existsByNickname("김철수")).willReturn(true);
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
		AdditionalInfoRequest request = new AdditionalInfoRequest("abc123@korea.com", "김신규", "공업", "가스");

		Optional<Member> member1 = Optional.ofNullable(createMember(null, "철수", "kakao123/kakao123@daum.net", null));
		given(memberRepository.findBySocialEmail("kakao123/kakao123@daum.net")).willReturn(member1);
		Member member = member1.get();
		given(memberRepository.save(any(Member.class))).willReturn(member);

		// when
		memberService.signUp(request, "kakao123/kakao123@daum.net");

		// then
		assertThat(member).extracting("officialEmail", "nickname", "jobGroup", "jobCategory")
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