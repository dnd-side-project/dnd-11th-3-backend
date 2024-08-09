package com.dnd.gongmuin.credit_history.fixture;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.credit_history.CreditHistory;
import com.dnd.gongmuin.credit_history.CreditType;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreditHistoryFixture {

	public static CreditHistory creditHistory(CreditType creditType, int reward, Member member){
		return CreditHistory.of(
			creditType,
			creditType.getDetail(),
			reward,
			member
		);
	}
}
