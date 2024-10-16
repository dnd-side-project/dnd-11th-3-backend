package com.dnd.gongmuin.common.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomFixture {

	public static ChatRoom chatRoom(
		QuestionPost questionPost,
		Member inquirer,
		Member answerer
	) {
		return ChatRoom.of(
			questionPost,
			inquirer,
			answerer
		);
	}

	public static ChatRoom chatRoom(
		Long id,
		QuestionPost questionPost,
		Member inquirer,
		Member answerer
	) {
		ChatRoom chatRoom = ChatRoom.of(
			questionPost,
			inquirer,
			answerer
		);
		ReflectionTestUtils.setField(chatRoom, "id", id);
		return chatRoom;
	}
}