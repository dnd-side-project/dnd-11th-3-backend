package com.dnd.gongmuin.credit_history.domain;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreditHistory extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "credit_history_id", nullable = false)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private CreditType type;

	@Column(name = "detail", nullable = false)
	private String detail;

	@Column(name = "amount", nullable = false)
	private int amount;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id", nullable = false) // 정합성 중요
	private Member member;

	private CreditHistory(CreditType type, String detail, int amount, Member member) {
		this.type = type;
		this.detail = detail;
		this.amount = amount;
		this.member = member;
	}

	public static CreditHistory of(CreditType type, String detail, int amount, Member member) {
		return new CreditHistory(type, detail, amount, member);
	}
}