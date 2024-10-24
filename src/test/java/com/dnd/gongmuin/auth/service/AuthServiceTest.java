package com.dnd.gongmuin.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.auth.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.auth.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.auth.dto.response.LogoutResponse;
import com.dnd.gongmuin.auth.dto.response.ReissueResponse;
import com.dnd.gongmuin.auth.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.JobCategory;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.notification.repository.NotificationRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;
import com.dnd.gongmuin.redis.util.RedisUtil;
import com.dnd.gongmuin.security.jwt.util.CookieUtil;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;
import com.dnd.gongmuin.security.service.OAuth2UnlinkService;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private OAuth2UnlinkService oAuth2UnlinkService;
	@Mock
	private CreditHistoryRepository creditHistoryRepository;
	@Mock
	private QuestionPostRepository questionPostRepository;
	@Mock
	private AnswerRepository answerRepository;
	@Mock
	private InteractionRepository interactionRepository;
	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private RedisUtil redisUtil;

	@Mock
	private CookieUtil cookieUtil;

	@InjectMocks
	private AuthService authService;

	@DisplayName("공무원 이메일이 존재하는지 체크한다.")
	@Test
	void isOfficialEmail() {
		// given
		Member kakaoMember = MemberFixture.member();

		// when
		boolean result = authService.isOfficialEmail(kakaoMember);

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
		ValidateNickNameResponse duplicatedNickname = authService.isDuplicatedNickname(request);

		// then
		assertThat(duplicatedNickname.isDuplicated()).isTrue();
	}

	@DisplayName("신규 회원은 추가 정보가 업데이트 된다.")
	@Test
	void signUp() {
		// given
		AdditionalInfoRequest request = new AdditionalInfoRequest("abc123@korea.com", "김신규", "공업", "일반기계");
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();

		Member member1 = MemberFixture.member3();
		given(memberRepository.findBySocialEmail(member1.getSocialEmail())).willReturn(
			Optional.of(member1));

		// when
		authService.signUp(request, member1.getSocialEmail(), mockResponse);

		// then
		assertThat(member1).extracting("officialEmail", "nickname", "jobGroup", "jobCategory")
			.containsExactlyInAnyOrder(
				"abc123@korea.com",
				"김신규",
				JobGroup.ENG,
				JobCategory.GME
			);
	}

	@DisplayName("로그인 회원은 로그아웃 할 수 있다.")
	@Test
	void logout() {
		// given
		Long fiveMinutes = 300_000L;

		Member principal = MemberFixture.member();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");

		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		mockRequest.setCookies(new Cookie("Authorization", "testtesttesttest"));

		given(cookieUtil.getCookieValue(mockRequest)).willReturn("testtesttesttest");
		given(tokenProvider.validateToken(anyString(), any(Date.class))).willReturn(true);
		given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
		given(tokenProvider.getExpiration(anyString(), any(Date.class))).willReturn(fiveMinutes);
		given(redisUtil.getValues(anyString())).willReturn("refresh");
		given(redisUtil.getValues(anyString())).willReturn("logout");

		willDoNothing().given(redisUtil).setValues(anyString(), anyString(), any(Duration.class));

		// when
		LogoutResponse response = authService.logout(mockRequest, mockResponse);

		// then
		assertThat(response.result()).isTrue();
		assertThat(mockResponse.getCookies()).isEmpty();
	}

	@DisplayName("refresh 토큰이 만료되지 않았다면 재발급 할 수 있다.")
	@Test
	void reissue() {
		// given
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		MockHttpServletResponse mockResponse = new MockHttpServletResponse();
		mockRequest.setCookies(new Cookie("Authorization", "testtesttesttest"));

		Member principal = MemberFixture.member();
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");

		given(cookieUtil.getCookieValue(mockRequest)).willReturn("testtesttesttest");
		given(cookieUtil.createCookie(anyString())).willReturn(new Cookie("Authorization", "reissueToken"));
		given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
		given(redisUtil.getValues(anyString())).willReturn("refreshToken");
		given(tokenProvider.generateAccessToken(any(CustomOauth2User.class), any(Date.class))).willReturn(
			"reissueToken");
		given(tokenProvider.generateRefreshToken(any(CustomOauth2User.class), any(Date.class))).willReturn(
			"reissueToken");

		// when
		ReissueResponse response = authService.reissue(mockRequest, mockResponse);
		Cookie[] cookies = mockResponse.getCookies();

		// then
		Assertions.assertAll(
			() -> assertThat(response.result()).isTrue(),
			() -> assertThat(cookies)
				.hasSize(1)
				.extracting(Cookie::getName, Cookie::getValue)
				.containsExactly(tuple("Authorization", "reissueToken"))
		);
	}

	@DisplayName("회원 삭제 시 크레딧 내역, 알림, 상호작용은 모두 삭제된다.")
	@Test
	void deleteMemberWithAssociatedDatas() {
		// given
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setCookies(new Cookie("Authorization", "testtesttesttest"));

		Member principal = MemberFixture.member(1L);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");

		given(cookieUtil.getCookieValue(mockRequest)).willReturn("testtesttesttest");
		given(tokenProvider.validateToken(anyString(), any(Date.class))).willReturn(true);
		given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
		given(redisUtil.getValues(anyString())).willReturn("delete");
		given(memberRepository.findByRole("ROLE_ANONYMOUS")).willReturn(Optional.of(principal));

		doNothing().when(creditHistoryRepository).deleteByMember(principal);
		doNothing().when(notificationRepository).deleteByMember(principal);
		doNothing().when(interactionRepository).deleteByMemberId(principal.getId());
		doNothing().when(memberRepository).delete(principal);

		// when
		authService.deleteMember(mockRequest);

		// then
		verify(creditHistoryRepository).deleteByMember(principal);
		verify(notificationRepository).deleteByMember(principal);
		verify(interactionRepository).deleteByMemberId(principal.getId());
		verify(memberRepository).delete(principal);
	}

	@DisplayName("회원 삭제 시 연관된 답변과 게시글은 모두 익명 회원 정보로 교체된다.")
	@Test
	void deleteMember() {
		// given
		MockHttpServletRequest mockRequest = new MockHttpServletRequest();
		mockRequest.setCookies(new Cookie("Authorization", "testtesttesttest"));

		Member principal = MemberFixture.member(1L);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "test");

		List<QuestionPost> mockQuestionPosts = List.of(QuestionPostFixture.questionPost(1L, principal));
		List<Answer> mockAnswers = List.of(AnswerFixture.answer(1L, principal));

		given(cookieUtil.getCookieValue(mockRequest)).willReturn("testtesttesttest");
		given(tokenProvider.validateToken(anyString(), any(Date.class))).willReturn(true);
		given(tokenProvider.getAuthentication(anyString())).willReturn(authentication);
		given(redisUtil.getValues(anyString())).willReturn("delete");
		given(memberRepository.findByRole("ROLE_ANONYMOUS")).willReturn(Optional.of(principal));

		// when
		authService.deleteMember(mockRequest);

		// then
		verify(questionPostRepository).updateQuestionPosts(any(), any());
		verify(answerRepository).updateAnswers(any(), any());
	}
}