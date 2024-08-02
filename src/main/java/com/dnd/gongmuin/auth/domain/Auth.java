package com.dnd.gongmuin.auth.domain;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.EnumType.*;

import com.dnd.gongmuin.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Auth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(STRING)
	@Column(name = "provider", nullable = false)
	private Provider provider;

	@Enumerated(STRING)
	@Column(name = "status", nullable = false)
	private AuthStatus status;

	@OneToOne
	@JoinColumn(name = "member_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Member member;

	@Builder
	private Auth(Provider provider, AuthStatus status, Member member) {
		this.provider = provider;
		this.status = status;
		this.member = member;
	}

	public static Auth of(Provider provider, AuthStatus status, Member member) {
		return Auth.builder()
			.provider(provider)
			.status(status)
			.member(member)
			.build();
	}

	public void updateStatus() {
		this.status = AuthStatus.OLD;
	}
}
