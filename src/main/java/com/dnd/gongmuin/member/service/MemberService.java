package com.dnd.gongmuin.member.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.dto.Oauth2Response;
import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.JobCategory;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.ValidNickNameRequest;
import com.dnd.gongmuin.member.dto.response.SignUpResponse;
import com.dnd.gongmuin.member.dto.response.ValidNickNameResponse;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
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

	public boolean isOfficialEmail(Member member) {
		return Objects.isNull(member.getOfficialEmail());
	}

	public ValidNickNameResponse isDuplicatedNickname(ValidNickNameRequest request) {
		boolean isDuplicate = memberRepository.existsByNickname(request.nickname());

		return new ValidNickNameResponse(isDuplicate);
	}

	@Transactional
	public SignUpResponse signUp(AdditionalInfoRequest request, String email) {
		Member findMember = memberRepository.findBySocialEmail(email)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));

		if (!isOfficialEmail(findMember)) {
			new NotFoundException(MemberErrorCode.NOT_FOUND_NEWMEMBER);
		}

		Member savedMember = updateAdditionalInfo(request, findMember);

		return new SignUpResponse(savedMember.getId());
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
}
