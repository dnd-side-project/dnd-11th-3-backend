package com.dnd.gongmuin.credit_history.fixture;

import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.member.domain.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditHistoryFixture {

	public static CreditHistory creditHistory(CreditType creditType, int reward, Member member) {
		return CreditHistory.of(
			creditType,
			creditType.getDetail(),
			reward,
			member
		);
	}
}
