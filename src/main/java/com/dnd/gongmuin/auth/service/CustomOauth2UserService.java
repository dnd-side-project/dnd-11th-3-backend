package com.dnd.gongmuin.auth.service;

import static com.dnd.gongmuin.auth.exception.AuthErrorCode.*;
import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;

import java.util.Objects;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.dnd.gongmuin.auth.dto.AuthMemberDto;
import com.dnd.gongmuin.auth.dto.CustomOauth2User;
import com.dnd.gongmuin.auth.dto.KakaoResponse;
import com.dnd.gongmuin.auth.dto.Oauth2Response;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Oauth2Response oauth2Response = null;

		if (Objects.equals(registrationId, "kakao")) {
			oauth2Response = new KakaoResponse(oAuth2User.getAttributes());
		} else {
			throw new OAuth2AuthenticationException(
				new OAuth2Error(UNSUPPORTED_SOCIAL_LOGIN.getCode()),
				UNSUPPORTED_SOCIAL_LOGIN.getMessage()
			);
		}

		String socialName = createSocialName(oauth2Response);
		Member findMember = memberRepository.findBySocialEmail(oauth2Response.getEmail());
		AuthMemberDto authMemberDto = new AuthMemberDto();

		if (Objects.isNull(findMember)) {
			// TODO : 신규 회원 추가 정보 필드(nickname, officialEmail ...) 어떻게 처리할지 의논
			Member newMember = Member.builder()
				.nickname("dummy")
				.socialName(socialName)
				.socialEmail(oauth2Response.getEmail())
				.officialEmail("dummy")
				.jobGroup(ENGINEERING)
				.jobCategory(GAS)
				.credit(10000)
				.build();
			Member savedMember = memberRepository.save(newMember);

			authMemberDto = createAuthMembmerDto(savedMember);
		} else if (!equalsSocialEmail(findMember, oauth2Response.getEmail())) {
			findMember.updateSocialEmail(oauth2Response.getEmail());
			Member savedMember = memberRepository.save(findMember);

			authMemberDto = createAuthMembmerDto(savedMember);
		}

		return new CustomOauth2User(authMemberDto);
	}

	private static AuthMemberDto createAuthMembmerDto(Member savedMember) {
		return AuthMemberDto.builder()
			.socialEmail(savedMember.getSocialEmail())
			.socialName(savedMember.getSocialName())
			.build();
	}

	private String createSocialName(Oauth2Response oauth2Response) {
		return oauth2Response.getProvider() + oauth2Response.getProviderId() + "/" + oauth2Response.getName();
	}

	private boolean equalsSocialEmail(Member findMember, String socialEmail) {
		if (Objects.equals(findMember.getSocialEmail(), socialEmail)) {
			return true;
		}
		return false;
	}

}

