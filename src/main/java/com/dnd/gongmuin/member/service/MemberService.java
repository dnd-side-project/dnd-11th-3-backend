package com.dnd.gongmuin.member.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.JobCategory;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.request.AdditionalInfoRequest;
import com.dnd.gongmuin.member.dto.request.ValidateNickNameRequest;
import com.dnd.gongmuin.member.dto.response.SignUpResponse;
import com.dnd.gongmuin.member.dto.response.ValidateNickNameResponse;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.security.oauth2.Oauth2Response;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

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
			new NotFoundException(MemberErrorCode.NOT_FOUND_NEW_MEMBER);
		}

		Member signUpMember = updateAdditionalInfo(request, findMember);

		return new SignUpResponse(signUpMember.getNickname());
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
		boolean result = memberRepository.existsByOfficialEmail(officialEmail);

		return result;
	}
}