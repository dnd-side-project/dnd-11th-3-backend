package com.dnd.gongmuin.member.service;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;

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
		Member member = memberRepository.findBySocialEmail(oauth2Response.getEmail())
			.map(m -> m.updateSocialEmail(oauth2Response.getEmail()))
			.orElse(createMemberFromOauth2Response(oauth2Response));

		return memberRepository.save(member);
	}

	public String parseProviderFromSocialName(Member member) {
		String socialName = member.getSocialName().toUpperCase();
		if (socialName.contains("KAKAO")) {
			return "KAKAO";
		} else if (socialName.contains("NAVER")) {
			return "NAVER";
		}
		throw new NotFoundException(AuthErrorCode.NOT_FOUND_PROVIDER);
	}

	private Member createMemberFromOauth2Response(Oauth2Response oauth2Response) {
		return Member.builder()
			.nickname("dummy")
			.socialName(oauth2Response.createSocialName())
			.socialEmail(oauth2Response.getEmail())
			.officialEmail("dummy")
			.jobGroup(ENGINEERING)
			.jobCategory(GAS)
			.credit(10000)
			.build();
	}

}
