package com.dnd.gongmuin.member.domain;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.util.Random;

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

	@Column(name = "role", nullable = false)
	private String role;

	@Column(name = "profile_image_no", nullable = false)
	private final int profileImageNo = setRandomNumber();

	@Builder(access = PRIVATE)
	private Member(String nickname, String socialName, JobGroup jobGroup, JobCategory jobCategory, String socialEmail,
		String officialEmail, int credit, String role) {
		this.nickname = nickname;
		this.socialName = socialName;
		this.jobGroup = jobGroup;
		this.jobCategory = jobCategory;
		this.socialEmail = socialEmail;
		this.officialEmail = officialEmail;
		this.credit = credit;
		this.role = role;
	}

	public static Member of(String socialName, String socialEmail, int credit) {
		return Member.builder()
			.socialName(socialName)
			.socialEmail(socialEmail)
			.credit(credit)
			.build();
	}

	public static Member of(String socialName, String socialEmail, int credit, String role) {
		return Member.builder()
			.socialName(socialName)
			.socialEmail(socialEmail)
			.credit(credit)
			.role(role)
			.build();
	}

	public static Member of(String nickname, String socialName, JobGroup jobGroup, JobCategory jobCategory,
		String socialEmail, String officialEmail, int credit, String role) {
		return Member.builder()
			.nickname(nickname)
			.socialName(socialName)
			.jobGroup(jobGroup)
			.jobCategory(jobCategory)
			.socialEmail(socialEmail)
			.officialEmail(officialEmail)
			.credit(credit)
			.role(role)
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
		this.role = "ROLE_USER";
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

	public void updateProfile(String nickname, JobGroup jobGroup, JobCategory jobCategory) {
		this.nickname = nickname;
		this.jobGroup = jobGroup;
		this.jobCategory = jobCategory;
	}

	private int setRandomNumber() {
		Random random = new Random();
		return random.nextInt(1, 10);
	}
}
