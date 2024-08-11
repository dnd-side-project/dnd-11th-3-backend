package com.dnd.gongmuin.member.service;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.LogoutRequest;
import com.dnd.gongmuin.member.dto.request.ReissueRequest;
import com.dnd.gongmuin.member.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.member.dto.response.LogoutResponse;
import com.dnd.gongmuin.member.dto.response.ReissueResponse;
import com.dnd.gongmuin.member.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.redis.util.RedisUtil;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private RedisUtil redisUtil;

	@InjectMocks
	private MemberService memberService;

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

		Member member1 = createMember(null, "철수", "kakao123/kakao123@daum.net", null);
		given(memberRepository.findBySocialEmail("kakao123/kakao123@daum.net")).willReturn(
			Optional.ofNullable(member1));

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

	@DisplayName("로그인 회원은 로그아웃 할 수 있다.")
	@Test
	void logout() {
		// given
		Member principal = MemberFixture.member();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");
		LogoutRequest request = new LogoutRequest("Bearer dsaooinbsoadi");
		Long fiveMinutes = 300_000L;

		given(tokenProvider.validateToken(anyString(), any(Date.class))).willReturn(true);
		given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
		given(tokenProvider.getExpiration(anyString(), any(Date.class))).willReturn(fiveMinutes);

		given(redisUtil.getValues(anyString())).willReturn("refresh");
		willDoNothing().given(redisUtil).setValues(anyString(), anyString(), any(Duration.class));

		given(redisUtil.getValues(anyString())).willReturn("logout");
		// when
		LogoutResponse response = memberService.logout(request);

		// then
		assertThat(response.result()).isTrue();
	}

	@DisplayName("refresh 토큰이 만료되지 않았다면 재발급 할 수 있다.")
	@Test
	void reissue() {
		// given
		ReissueRequest request = new ReissueRequest("Bearer dafdfweqe");
		Member principal = MemberFixture.member();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");

		given(tokenProvider.validateToken(anyString(), any(Date.class))).willReturn(true);
		given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);

		given(redisUtil.getValues(anyString())).willReturn("refreshToken");
		given(tokenProvider.generateAccessToken(any(CustomOauth2User.class), any(Date.class))).willReturn(
			"reissueToken");
		given(tokenProvider.generateRefreshToken(any(CustomOauth2User.class), any(Date.class))).willReturn(
			"reissueToken");

		// when
		ReissueResponse response = memberService.reissue(request);

		// then
		assertThat(response.accessToken()).isEqualTo("reissueToken");
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