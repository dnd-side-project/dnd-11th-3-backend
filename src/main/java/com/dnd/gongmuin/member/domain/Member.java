package com.dnd.gongmuin.member.domain;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;

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

	@Column(name = "nickname", nullable = false)
	private String nickname;

	@Column(name = "social_name", nullable = false)
	private String socialName;

	@Enumerated(STRING)
	@Column(name = "job_group", nullable = false)
	private JobGroup jobGroup;

	@Enumerated(STRING)
	@Column(name = "job_category", nullable = false)
	private JobCategory jobCategory;

	@Column(name = "social_email", nullable = false)
	private String socialEmail;

	@Column(name = "official_email", nullable = false)
	private String officialEmail;

	@Column(name = "credit", nullable = false)
	private int credit;

	@Builder
	public Member(String nickname, String socialName, JobGroup jobGroup, JobCategory jobCategory, String socialEmail,
		String officialEmail, int credit) {
		this.nickname = nickname;
		this.socialName = socialName;
		this.jobGroup = jobGroup;
		this.jobCategory = jobCategory;
		this.socialEmail = socialEmail;
		this.officialEmail = officialEmail;
		this.credit = credit;
	}

	public void updateSocialEmail(String socialEmail) {
		this.socialEmail = socialEmail;
	}

}
