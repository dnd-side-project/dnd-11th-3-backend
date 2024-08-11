package com.dnd.gongmuin.member.service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.JobCategory;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.LogoutRequest;
import com.dnd.gongmuin.member.dto.request.ReissueRequest;
import com.dnd.gongmuin.member.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.member.dto.response.LogoutResponse;
import com.dnd.gongmuin.member.dto.response.ReissueResponse;
import com.dnd.gongmuin.member.dto.response.SignUpResponse;
import com.dnd.gongmuin.member.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.redis.util.RedisUtil;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.AuthInfo;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;
import com.dnd.gongmuin.security.oauth2.Oauth2Response;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private static final String TOKEN_PREFIX = "Bearer ";
	private static final String LOGOUT = "logout";
	private final MemberRepository memberRepository;
	private final TokenProvider tokenProvider;
	private final RedisUtil redisUtil;

	public Member saveOrUpdate(Oauth2Response oauth2Response) {
		Member member = memberRepository.findBySocialEmail(oauth2Response.createSocialEmail())
			.map(m -> {
				m.updateSocialEmail(oauth2Response.createSocialEmail());
				return m;
			})
			.orElse(createMemberFromOauth2Response(oauth2Response));

		return memberRepository.save(member);
	}

	public String parseProviderFromSocialEmail(Member member) {
		String socialEmail = member.getSocialEmail().toUpperCase();
		if (socialEmail.contains("KAKAO")) {
			return "KAKAO";
		} else if (socialEmail.contains("NAVER")) {
			return "NAVER";
		}
		throw new NotFoundException(AuthErrorCode.NOT_FOUND_PROVIDER);
	}

	private Member createMemberFromOauth2Response(Oauth2Response oauth2Response) {
		return Member.of(oauth2Response.getName(), oauth2Response.createSocialEmail(), 10000);
	}

	public boolean isOfficialEmail(Member member) {
		return Objects.isNull(member.getOfficialEmail());
	}

	@Transactional(readOnly = true)
	public ValidateNickNameResponse isDuplicatedNickname(ValidateNickNameRequest request) {
		boolean isDuplicate = memberRepository.existsByNickname(request.nickname());

		return new ValidateNickNameResponse(isDuplicate);
	}

	@Transactional
	public SignUpResponse signUp(AdditionalInfoRequest request, String email) {
		Member findMember = memberRepository.findBySocialEmail(email)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));

		if (!isOfficialEmail(findMember)) {
			throw new NotFoundException(MemberErrorCode.NOT_FOUND_NEW_MEMBER);
		}

		updateAdditionalInfo(request, findMember);

		return new SignUpResponse(findMember.getNickname());
	}

	public Member getMemberBySocialEmail(String socialEmail) {
		return memberRepository.findBySocialEmail(socialEmail)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));
	}

	private Member updateAdditionalInfo(AdditionalInfoRequest request, Member findMember) {
		findMember.updateAdditionalInfo(
			request.nickname(),
			request.officialEmail(),
			JobGroup.of(request.jobGroup()),
			JobCategory.of(request.jobCategory())
		);

		return memberRepository.save(findMember);
	}

	@Transactional(readOnly = true)
	public boolean isOfficialEmailExists(String officialEmail) {
		return memberRepository.existsByOfficialEmail(officialEmail);
	}

	public LogoutResponse logout(LogoutRequest request) {
		String accessToken = request.accessToken().substring(TOKEN_PREFIX.length());

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

		return new LogoutResponse(true);
	}

	public ReissueResponse reissue(ReissueRequest request) {
		String accessToken = request.accessToken().substring(TOKEN_PREFIX.length());

		if (!tokenProvider.validateToken(accessToken, new Date())) {
			throw new ValidationException(AuthErrorCode.UNAUTHORIZED_TOKEN);
		}

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
			AuthInfo.of(member.getSocialName(), member.getSocialEmail()));
		String reissuedAccessToken = tokenProvider.generateAccessToken(customUser, new Date());
		tokenProvider.generateRefreshToken(customUser, new Date());

		return new ReissueResponse(reissuedAccessToken);
	}
}