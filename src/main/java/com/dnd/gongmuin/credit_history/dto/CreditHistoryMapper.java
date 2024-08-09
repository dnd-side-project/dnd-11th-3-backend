package com.dnd.gongmuin.credit_history.dto;

import com.dnd.gongmuin.credit_history.CreditHistory;
import com.dnd.gongmuin.credit_history.CreditType;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditHistoryMapper {
	public static CreditHistory toCredit(CreditType creditType, QuestionPost questionPost, Member member) {
		return CreditHistory.of(creditType, creditType.getDetail(), questionPost.getReward(), member);
	}
}
