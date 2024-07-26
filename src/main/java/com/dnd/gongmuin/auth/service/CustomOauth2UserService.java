package com.dnd.gongmuin.auth.service;

import static com.dnd.gongmuin.auth.exception.AuthErrorCode.*;

import java.util.Objects;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.dto.AuthDto;
import com.dnd.gongmuin.auth.dto.CustomOauth2User;
import com.dnd.gongmuin.auth.dto.KakaoResponse;
import com.dnd.gongmuin.auth.dto.Oauth2Response;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;
	private final MemberService memberService;
	private final AuthService authService;

	@Transactional
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

		Member savedMember = saveOrUpdate(oauth2Response);
		authService.saveOrUpdate(savedMember);

		AuthDto authDto = AuthDto.builder()
			.socialEmail(savedMember.getSocialEmail())
			.socialName(savedMember.getSocialName())
			.build();
		return new CustomOauth2User(authDto);
	}

	private Member saveOrUpdate(Oauth2Response oauth2Response) {
		Member member = memberRepository.findBySocialEmail(oauth2Response.getEmail())
			.map(m -> m.updateSocialEmail(oauth2Response.getEmail()))
			.orElse(memberService.createMemberFromOauth2Response(oauth2Response));

		return memberRepository.save(member);
	}

}

