package com.dnd.gongmuin.chat.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.domain.ChatStatus;
import com.dnd.gongmuin.chat.dto.request.CreateChatRoomRequest;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.chat.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.fixture.ChatMessageFixture;
import com.dnd.gongmuin.common.fixture.ChatRoomFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[ChatMessage 통합 테스트]")
class ChatRoomControllerTest extends ApiTestSupport {

	private static final int CHAT_REWARD = 2000;

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@AfterEach
	void teardown() {
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
		chatRoomRepository.deleteAll();
	}

	@DisplayName("[채팅방 아이디로 메시지를 조회할 수 있다.]")
	@Test
	void getChatMessages() throws Exception {
		List<ChatMessage> chatMessages = chatMessageRepository.saveAll(List.of(
			ChatMessageFixture.chatMessage(),
			ChatMessageFixture.chatMessage()
		));
		mockMvc.perform(get("/api/chat-messages/{chatRoomId}", 1L)
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].senderId").value(chatMessages.get(0).getMemberId()))
			.andExpect(jsonPath("$.content[0].content").value(chatMessages.get(0).getContent()))
			.andExpect(jsonPath("$.content[0].type").value(chatMessages.get(0).getType().getLabel()));
	}

	@DisplayName("[채팅방을 생성할 수 있다.]")
	@Test
	void createChatRoom() throws Exception {
		Member answerer = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		CreateChatRoomRequest request = new CreateChatRoomRequest(questionPost.getId(), answerer.getId());

		mockMvc.perform(post("/api/chat-rooms")
				.cookie(accessToken)
				.content(toJson(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@DisplayName("[답변자가 채팅 요청을 수락할 수 있다.]")
	@Test
	void acceptChatRoom() throws Exception {
		Member inquirer = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(inquirer));
		ChatRoom chatRoom = chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPost, inquirer, loginMember));
		int previousAnswererCredit = chatRoom.getAnswerer().getCredit();

		mockMvc.perform(patch("/api/chat-rooms/{chatRoomId}/accept", 1L)
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chatStatus").value(ChatStatus.ACCEPTED.getLabel()))
			.andExpect(jsonPath("$.credit").value(previousAnswererCredit + CHAT_REWARD));
	}
}