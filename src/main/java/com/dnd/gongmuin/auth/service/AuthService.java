package com.dnd.gongmuin.auth.service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.auth.dto.request.TempSignInRequest;
import com.dnd.gongmuin.auth.dto.request.TempSignUpRequest;
import com.dnd.gongmuin.auth.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.auth.dto.response.LogoutResponse;
import com.dnd.gongmuin.auth.dto.response.ReissueResponse;
import com.dnd.gongmuin.auth.dto.response.SignUpResponse;
import com.dnd.gongmuin.auth.dto.response.TempSignResponse;
import com.dnd.gongmuin.auth.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.JobCategory;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.redis.util.RedisUtil;
import com.dnd.gongmuin.security.jwt.util.CookieUtil;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.AuthInfo;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final String LOGOUT = "logout";
	private final TokenProvider tokenProvider;
	private final MemberRepository memberRepository;
	private final CookieUtil cookieUtil;
	private final RedisUtil redisUtil;

	@Transactional
	public TempSignResponse tempSignUp(TempSignUpRequest tempSignUpRequest, HttpServletResponse response) {
		Date now = new Date();
		Member member = Member.of(
			tempSignUpRequest.socialName(),
			"kakao/" + tempSignUpRequest.socialEmail(),
			10000,
			"ROLE_GUEST"
		);

		if (memberRepository.existsBySocialEmail(member.getSocialEmail())) {
			throw new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER);
		}

		memberRepository.save(member);

		AuthInfo authInfo = AuthInfo.of(member.getSocialName(), member.getSocialEmail(), member.getRole());
		CustomOauth2User customOauth2User = new CustomOauth2User(authInfo);

		tokenProvider.generateRefreshToken(customOauth2User, now);
		String accessToken = tokenProvider.generateAccessToken(customOauth2User, now);
		response.addCookie(cookieUtil.createCookie(accessToken));

		return new TempSignResponse(true);
	}

	@Transactional
	public TempSignResponse tempSignIn(TempSignInRequest tempSignInRequest, HttpServletResponse response) {
		Date now = new Date();

		String prefixSocialEmail = "kakao/" + tempSignInRequest.socialEmail();

		Member member = memberRepository.findBySocialEmail(prefixSocialEmail)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));

		AuthInfo authInfo = AuthInfo.of(member.getSocialName(), member.getSocialEmail(), member.getRole());
		CustomOauth2User customOauth2User = new CustomOauth2User(authInfo);

		tokenProvider.generateRefreshToken(customOauth2User, now);
		String accessToken = tokenProvider.generateAccessToken(customOauth2User, now);
		response.addCookie(cookieUtil.createCookie(accessToken));

		return new TempSignResponse(true);
	}

	@Transactional(readOnly = true)
	public ValidateNickNameResponse isDuplicatedNickname(ValidateNickNameRequest request) {
		boolean isDuplicated = memberRepository.existsByNickname(request.nickname());

		return new ValidateNickNameResponse(isDuplicated);
	}

	@Transactional
	public SignUpResponse signUp(AdditionalInfoRequest request, String email, HttpServletResponse response) {
		Member foundMember = memberRepository.findBySocialEmail(email)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));

		if (!isOfficialEmail(foundMember)) {
			throw new NotFoundException(MemberErrorCode.NOT_FOUND_NEW_MEMBER);
		}

		updateAdditionalInfo(request, foundMember);

		cookieUtil.deleteCookie(response);

		return new SignUpResponse(foundMember.getNickname());
	}

	public LogoutResponse logout(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.getCookieValue(request);

		if (!tokenProvider.validateToken(accessToken, new Date())) {
			throw new ValidationException(AuthErrorCode.UNAUTHORIZED_TOKEN);
		}

		Authentication authentication = tokenProvider.getAuthentication(accessToken);
		Member member = (Member)authentication.getPrincipal();

		if (!Objects.isNull(redisUtil.getValues("RT:" + member.getSocialEmail()))) {
			redisUtil.deleteValues("RT:" + member.getSocialEmail());
		}

		Long expiration = tokenProvider.getExpiration(accessToken, new Date());
		redisUtil.setValues(accessToken, LOGOUT, Duration.ofMillis(expiration));

		String values = redisUtil.getValues(accessToken);
		if (!Objects.equals(values, LOGOUT)) {
			throw new NotFoundException(MemberErrorCode.LOGOUT_FAILED);
		}

		cookieUtil.deleteCookie(response);

		return new LogoutResponse(true);
	}

	public ReissueResponse reissue(HttpServletRequest request, HttpServletResponse response) {
		String accessToken = cookieUtil.getCookieValue(request);

		// 로그아웃 토큰 처리
		if ("logout".equals(redisUtil.getValues(accessToken))) {
			throw new ValidationException(AuthErrorCode.UNAUTHORIZED_TOKEN);
		}

		Authentication authentication = tokenProvider.getAuthentication(accessToken);
		Member member = (Member)authentication.getPrincipal();

		String refreshToken = redisUtil.getValues("RT:" + member.getSocialEmail());

		// 로그아웃 또는 토큰 만료 경우 처리
		if ("false".equals(refreshToken)) {
			throw new ValidationException(AuthErrorCode.UNAUTHORIZED_TOKEN);
		}

		CustomOauth2User customUser = new CustomOauth2User(
			AuthInfo.of(member.getSocialName(), member.getSocialEmail(), member.getRole()));
		String reissuedAccessToken = tokenProvider.generateAccessToken(customUser, new Date());
		tokenProvider.generateRefreshToken(customUser, new Date());

		response.addCookie(cookieUtil.createCookie(reissuedAccessToken));

		return new ReissueResponse(true);
	}

	public boolean isOfficialEmail(Member member) {
		return Objects.isNull(member.getOfficialEmail());
	}

	private void updateAdditionalInfo(AdditionalInfoRequest request, Member findMember) {
		findMember.updateAdditionalInfo(
			request.nickname(),
			request.officialEmail(),
			JobGroup.from(request.jobGroup()),
			JobCategory.from(request.jobCategory())
		);
	}

}
