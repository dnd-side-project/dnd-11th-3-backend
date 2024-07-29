package com.dnd.gongmuin.member.service;

import org.springframework.stereotype.Service;

import com.dnd.gongmuin.auth.dto.Oauth2Response;
import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	public Member saveOrUpdate(Oauth2Response oauth2Response) {
		Member member = memberRepository.findBySocialEmail(oauth2Response.createSocialEmail())
			.map(m -> m.updateSocialEmail(oauth2Response.createSocialEmail()))
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
		return Member.builder()
			.socialName(oauth2Response.getName())
			.socialEmail(oauth2Response.createSocialEmail())
			.credit(10000)
			.build();
	}

}
