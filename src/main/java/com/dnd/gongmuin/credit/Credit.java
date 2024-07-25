package com.dnd.gongmuin.credit;

import static jakarta.persistence.FetchType.*;

import com.dnd.gongmuin.common.entity.TimeBaseEntity;
import com.dnd.gongmuin.member.domain.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Credit extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "credit_id", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private CreditType type;

	@Enumerated(EnumType.STRING)
	@Column(name = "detail", nullable = false)
	private CreditDetail detail;

	@Column(name = "amount", nullable = false)
	private int amount;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id", nullable = false) // 정합성 중요
	private Member member;

	@Builder
	public Credit(CreditType type, CreditDetail detail, int amount, Member member) {
		this.type = type;
		this.detail = detail;
		this.amount = amount;
		this.member = member;
	}
}
