package com.dnd.gongmuin.member.domain;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.member.exception.MemberErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(name = "nickname")
	private String nickname;

	@Column(name = "social_name", nullable = false)
	private String socialName;

	@Enumerated(STRING)
	@Column(name = "job_group")
	private JobGroup jobGroup;

	@Enumerated(STRING)
	@Column(name = "job_category")
	private JobCategory jobCategory;

	@Column(name = "social_email", nullable = false)
	private String socialEmail;

	@Column(name = "official_email")
	private String officialEmail;

	@Column(name = "credit", nullable = false)
	private int credit;

	@Builder
	private Member(String nickname, String socialName, JobGroup jobGroup, JobCategory jobCategory, String socialEmail,
		String officialEmail, int credit) {
		this.nickname = nickname;
		this.socialName = socialName;
		this.jobGroup = jobGroup;
		this.jobCategory = jobCategory;
		this.socialEmail = socialEmail;
		this.officialEmail = officialEmail;
		this.credit = credit;
	}

	public static Member of(String socialName, String socialEmail, int credit) {
		return Member.builder()
			.socialName(socialName)
			.socialEmail(socialEmail)
			.credit(credit)
			.build();
	}

	public static Member of(String nickname, String socialName, JobGroup jobGroup, JobCategory jobCategory,
		String socialEmail, String officialEmail, int credit) {
		return Member.builder()
			.nickname(nickname)
			.socialName(socialName)
			.jobGroup(jobGroup)
			.jobCategory(jobCategory)
			.socialEmail(socialEmail)
			.officialEmail(officialEmail)
			.credit(credit)
			.build();
	}

	public void updateSocialEmail(String socialEmail) {
		this.socialEmail = socialEmail;
	}

	public void updateAdditionalInfo(String nickname, String officialEmail,
		JobGroup jobGroup, JobCategory jobCategory) {
		this.nickname = nickname;
		this.officialEmail = officialEmail;
		this.jobGroup = jobGroup;
		this.jobCategory = jobCategory;
	}

	public void decreaseCredit(int credit) {
		if (this.credit < credit) {
			throw new ValidationException(MemberErrorCode.NOT_ENOUGH_CREDIT);
		}
		this.credit -= credit;
	}

	public void increaseCredit(int credit) {
		this.credit += credit;
	}

}
