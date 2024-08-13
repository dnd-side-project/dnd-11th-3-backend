package com.dnd.gongmuin.auth.service;

import java.util.Date;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.domain.Auth;
import com.dnd.gongmuin.auth.domain.AuthStatus;
import com.dnd.gongmuin.auth.domain.Provider;
import com.dnd.gongmuin.auth.dto.TempLoginRequest;
import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.auth.repository.AuthRepository;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.member.service.MemberService;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.AuthInfo;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthRepository authRepository;
	private final MemberService memberService;
	private final TokenProvider tokenProvider;
	private final MemberRepository memberRepository;

	public void saveOrUpdate(Member savedMember) {
		Auth findedOrCreatedAuth = authRepository.findByMember(savedMember)
			.map(auth -> {
				if (!memberService.isOfficialEmail(savedMember)) {
					auth.updateStatus();
				}
				return auth;
			})
			.orElse(createAuth(savedMember));

		authRepository.save(findedOrCreatedAuth);
	}

	public boolean isAuthStatusOld(Member member) {
		Auth findAuth = authRepository.findByMember(member)
			.orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_AUTH));

		return Objects.equals(findAuth.getStatus(), AuthStatus.OLD);
	}

	private Auth createAuth(Member savedMember) {
		Provider provider = memberService.parseProviderFromSocialEmail(savedMember);

		return Auth.of(provider, AuthStatus.NEW, savedMember);
	}

	@Transactional
	public String swaggerToken(TempLoginRequest tempLoginRequest) {
		Date now = new Date();
		Member member = Member.of(tempLoginRequest.socialName(), "kakao/" + tempLoginRequest.socialEmail(), 10000);

		if (memberRepository.existsBySocialEmail(member.getSocialEmail())) {
			throw new NotFoundException(MemberErrorCode.NOT_FOUND_NEW_MEMBER);
		}
		memberRepository.save(member);
		saveOrUpdate(member);

		AuthInfo authInfo = AuthInfo.of(member.getSocialName(), member.getSocialEmail());
		CustomOauth2User customOauth2User = new CustomOauth2User(authInfo);

		tokenProvider.generateRefreshToken(customOauth2User, now);
		return tokenProvider.generateAccessToken(customOauth2User, now);
	}

}
