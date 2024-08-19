package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.querydsl.core.annotations.QueryProjection;

public record CreditHistoryByMemberResponse(
	Long id,
	String type,
	String detail,
	int amount
) {
	@QueryProjection
	public CreditHistoryByMemberResponse(CreditHistory creditHistory) {
		this(
			creditHistory.getId(),
			creditHistory.getType().getDetail(),
			creditHistory.getDetail(),
			creditHistory.getAmount()
		);
	}
}
