package com.dnd.gongmuin.security.service;

import static com.dnd.gongmuin.auth.exception.AuthErrorCode.*;
import static com.dnd.gongmuin.member.domain.Provider.*;

import java.util.Objects;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.service.MemberService;
import com.dnd.gongmuin.security.oauth2.AuthInfo;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;
import com.dnd.gongmuin.security.oauth2.KakaoResponse;
import com.dnd.gongmuin.security.oauth2.NaverResponse;
import com.dnd.gongmuin.security.oauth2.Oauth2Response;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

	private final MemberService memberService;

	@Transactional
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		String oauth2AccessToken = userRequest.getAccessToken().getTokenValue();

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Oauth2Response oauth2Response = null;

		if (Objects.equals(registrationId, KAKAO.getLabel())) {
			oauth2Response = new KakaoResponse(oAuth2User.getAttributes(), oauth2AccessToken);
		} else if (Objects.equals(registrationId, NAVER.getLabel())) {
			oauth2Response = new NaverResponse(oAuth2User.getAttributes(), oauth2AccessToken);
		} else {
			throw new OAuth2AuthenticationException(
				new OAuth2Error(UNSUPPORTED_SOCIAL_LOGIN.getCode()),
				UNSUPPORTED_SOCIAL_LOGIN.getMessage()
			);
		}

		Member savedMember = memberService.saveOrUpdate(oauth2Response);

		AuthInfo authInfo = AuthInfo.of(
			savedMember.getSocialName(),
			savedMember.getSocialEmail(),
			savedMember.getRole()
		);
		return new CustomOauth2User(authInfo);
	}

}

