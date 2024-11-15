package com.dnd.gongmuin.common.fixture;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatInquiryFixture {

	public static ChatInquiry chatInquiry(
		QuestionPost questionPost,
		Member inquirer,
		Member answerer,
		String message
	) {
		return ChatInquiry.of(
			questionPost,
			inquirer,
			answerer,
			message
		);
	}
}
