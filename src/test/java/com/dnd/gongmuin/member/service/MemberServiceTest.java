package com.dnd.gongmuin.member.service;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.gongmuin.auth.domain.Provider;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.UpdateMemberProfileRequest;
import com.dnd.gongmuin.member.dto.response.MemberProfileResponse;
import com.dnd.gongmuin.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private MemberService memberService;

	@DisplayName("조합된 소셜 이메일 부분 중 공급자 부분을 얻을 수 있다.")
	@Test
	void parseProviderFromSocialEmail() {
		// given
		Member kakaoMember = createMember("김철수", "철수", "kakao123/kim@daum.net", "abc123@korea.com");
		Member naverMember = createMember("박철수", "철수", "naver123/park@naver.com", "abc321@korea.com");

		try (MockedStatic<Provider> mockedProvider = mockStatic(Provider.class)) {
			mockedProvider.when(() -> Provider.fromSocialEmail("kakao123/kim@daum.net")).thenReturn(Provider.KAKAO);
			mockedProvider.when(() -> Provider.fromSocialEmail("naver123/park@naver.com")).thenReturn(Provider.NAVER);

			// when
			Provider kakaoProvider = memberService.parseProviderFromSocialEmail(kakaoMember);
			Provider naverProvider = memberService.parseProviderFromSocialEmail(naverMember);

			// then
			assertThat(kakaoProvider).isEqualTo(Provider.KAKAO);
			assertThat(naverProvider).isEqualTo(Provider.NAVER);
		}
	}

	@DisplayName("소셜 이메일로 회원을 찾는다.")
	@Test
	void getMemberBySocialEmail() {
		// given
		Member member = createMember(null, "김회원", "kakao123/kakao123@daum.net", null);
		given(memberRepository.findBySocialEmail("kakao123/kakao123@daum.net")).willReturn(Optional.ofNullable(member));

		// when
		Member findMember = memberService.getMemberBySocialEmail("kakao123/kakao123@daum.net");

		// then
		assertThat(findMember).extracting("socialName", "socialEmail")
			.containsExactlyInAnyOrder(
				"김회원",
				"kakao123/kakao123@daum.net"
			);
	}

	@DisplayName("로그인 된 사용자 프로필 정보를 조회한다.")
	@Test
	void getMemberProfile() {
		// given
		Member member = MemberFixture.member();
		given(memberRepository.findByOfficialEmail(anyString())).willReturn(member);

		// when
		MemberProfileResponse memberProfile = memberService.getMemberProfile(member);

		// then
		assertAll(
			() -> assertThat(memberProfile.nickname()).isEqualTo(member.getNickname()),
			() -> assertThat(memberProfile.jobGroup()).isEqualTo(member.getJobGroup().getLabel()),
			() -> assertThat(memberProfile.jobCategory()).isEqualTo(member.getJobCategory().getLabel()),
			() -> assertThat(memberProfile.credit()).isEqualTo(member.getCredit())
		);
	}

	@DisplayName("로그인 된 사용자 프로필 정보 조회 시 회원을 찾을 수 없으면 예외가 발생한다.")
	@Test
	void getMemberProfileThrowException() {
		// given
		Member member = MemberFixture.member();

		// when  // then
		assertThrows(NotFoundException.class, () -> memberService.getMemberProfile(member));
	}

	@DisplayName("로그인 된 사용자 프로필을 수정한다.")
	@Test
	void updateMemberProfile() {
		// given
		Member member = MemberFixture.member();
		UpdateMemberProfileRequest request = new UpdateMemberProfileRequest("박회원", "공업", "가스");

		given(memberRepository.findByOfficialEmail(anyString())).willReturn(member);

		// when
		MemberProfileResponse response = memberService.updateMemberProfile(request, member);

		// then
		assertAll(
			() -> assertThat(response.nickname()).isEqualTo(request.nickname()),
			() -> assertThat(response.jobGroup()).isEqualTo(request.jobGroup()),
			() -> assertThat(response.jobCategory()).isEqualTo(request.jobCategory()),
			() -> assertThat(response.credit()).isEqualTo(member.getCredit())
		);
	}

	@DisplayName("로그인 된 사용자 프로필 수정 실패 시 예외가 발생한다.")
	@Test
	void updateMemberProfileThrowException() {
		// given
		Member member = MemberFixture.member();
		UpdateMemberProfileRequest request = new UpdateMemberProfileRequest("박회원", "공업", "가스");

		// when  // then
		assertThrows(ValidationException.class, () -> memberService.updateMemberProfile(request, member));

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