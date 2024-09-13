package com.dnd.gongmuin.member.dto.response;

import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.querydsl.core.annotations.QueryProjection;

public record CreditHistoryResponse(
	Long id,
	String type,
	String detail,
	int amount,
	int profileImageNo,
	String createdAt
) {
	@QueryProjection
	public CreditHistoryResponse(CreditHistory creditHistory, int profileImageNo) {
		this(
			creditHistory.getId(),
			creditHistory.getType().getLabel(),
			creditHistory.getDetail(),
			creditHistory.getAmount(),
			profileImageNo,
			creditHistory.getCreatedAt().toString()
		);
	}
}
