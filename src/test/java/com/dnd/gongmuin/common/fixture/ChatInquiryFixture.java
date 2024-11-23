package com.dnd.gongmuin.common.fixture;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

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

	public static ChatInquiry chatInquiry(
		Long id,
		QuestionPost questionPost,
		Member inquirer,
		Member answerer,
		String message
	) {
		ChatInquiry chatInquiry = ChatInquiry.of(
			questionPost,
			inquirer,
			answerer,
			message
		);
		ReflectionTestUtils.setField(chatInquiry, "id", id);
		ReflectionTestUtils.setField(chatInquiry, "createdAt", LocalDateTime.now());
		return chatInquiry;
	}
}
