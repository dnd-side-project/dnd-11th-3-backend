package com.dnd.gongmuin.member.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.domain.JobCategory;
import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.domain.Provider;
import com.dnd.gongmuin.member.dto.MemberMapper;
import com.dnd.gongmuin.member.dto.request.UpdateMemberProfileRequest;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsResponse;
import com.dnd.gongmuin.member.dto.response.BookmarksResponse;
import com.dnd.gongmuin.member.dto.response.CreditHistoryResponse;
import com.dnd.gongmuin.member.dto.response.MemberInformationResponse;
import com.dnd.gongmuin.member.dto.response.MemberProfileResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsResponse;
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
			.orElseGet(() -> createMemberFromOauth2Response(oauth2Response));

		return memberRepository.save(member);
	}

	public Provider parseProviderFromSocialEmail(Member member) {
		String socialEmail = member.getSocialEmail();
		return Provider.fromSocialEmail(socialEmail);
	}

	private Member createMemberFromOauth2Response(Oauth2Response oauth2Response) {
		return Member.of(oauth2Response.getName(), oauth2Response.createSocialEmail(), 10000, "ROLE_GUEST");
	}

	public Member getMemberBySocialEmail(String socialEmail) {
		return memberRepository.findBySocialEmail(socialEmail)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));
	}

	@Transactional(readOnly = true)
	public boolean isOfficialEmailExists(String officialEmail) {
		return memberRepository.existsByOfficialEmail(officialEmail);
	}

	@Transactional(readOnly = true)
	public MemberProfileResponse getMemberProfile(Member member) {
		try {
			Member findMember = memberRepository.findByOfficialEmail(member.getOfficialEmail());
			return MemberMapper.toMemberProfileResponse(findMember);
		} catch (Exception e) {
			throw new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER);
		}
	}

	@Transactional
	public MemberProfileResponse updateMemberProfile(UpdateMemberProfileRequest request, Member member) {
		try {
			Member findMember = memberRepository.findByOfficialEmail(member.getOfficialEmail());
			JobGroup jobGroup = JobGroup.from(request.jobGroup());
			JobCategory jobCategory = JobCategory.from(request.jobCategory());

			findMember.updateProfile(
				request.nickname(),
				jobGroup,
				jobCategory
			);

			return MemberMapper.toMemberProfileResponse(findMember);
		} catch (Exception e) {
			throw new ValidationException(MemberErrorCode.UPDATE_PROFILE_FAILED);
		}
	}

	public PageResponse<QuestionPostsResponse> getQuestionPostsByMember(
		Member member, Pageable pageable) {
		try {
			Slice<QuestionPostsResponse> responsePage =
				memberRepository.getQuestionPostsByMember(member, pageable);

			return PageMapper.toPageResponse(responsePage);
		} catch (Exception e) {
			throw new NotFoundException(MemberErrorCode.QUESTION_POSTS_BY_MEMBER_FAILED);
		}
	}

	public PageResponse<AnsweredQuestionPostsResponse> getAnsweredQuestionPostsByMember(
		Member member, Pageable pageable) {
		try {
			Slice<AnsweredQuestionPostsResponse> responsePage =
				memberRepository.getAnsweredQuestionPostsByMember(member, pageable);

			return PageMapper.toPageResponse(responsePage);
		} catch (Exception e) {
			throw new NotFoundException(MemberErrorCode.QUESTION_POSTS_BY_MEMBER_FAILED);
		}
	}

	public PageResponse<BookmarksResponse> getBookmarksByMember(
		Member member, Pageable pageable) {
		try {
			Slice<BookmarksResponse> responsePage =
				memberRepository.getBookmarksByMember(member, pageable);

			return PageMapper.toPageResponse(responsePage);
		} catch (Exception e) {
			throw new NotFoundException(MemberErrorCode.QUESTION_POSTS_BY_MEMBER_FAILED);
		}
	}

	public PageResponse<CreditHistoryResponse> getCreditHistoryByMember(String type, Member member,
		Pageable pageable) {
		try {
			Slice<CreditHistoryResponse> responsePage =
				memberRepository.getCreditHistoryByMember(type, member, pageable);

			return PageMapper.toPageResponse(responsePage);
		} catch (Exception e) {
			throw new NotFoundException(MemberErrorCode.QUESTION_POSTS_BY_MEMBER_FAILED);
		}

	}

	public MemberInformationResponse getMemberInformation(Member member) {
		try {
			Member findMember = memberRepository.findByOfficialEmail(member.getOfficialEmail());
			return MemberMapper.toMemberInformationResponse(findMember);
		} catch (Exception e) {
			throw new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER);
		}
	}
}