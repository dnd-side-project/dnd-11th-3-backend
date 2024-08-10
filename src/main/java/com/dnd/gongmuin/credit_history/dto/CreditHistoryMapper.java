package com.dnd.gongmuin.credit_history.dto;

import com.dnd.gongmuin.credit_history.CreditHistory;
import com.dnd.gongmuin.credit_history.CreditType;
import com.dnd.gongmuin.member.domain.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditHistoryMapper {
	public static CreditHistory toCreditHistory(CreditType creditType, int reward, Member member) {
		return CreditHistory.of(creditType, creditType.getDetail(), reward, member);
	}
}
