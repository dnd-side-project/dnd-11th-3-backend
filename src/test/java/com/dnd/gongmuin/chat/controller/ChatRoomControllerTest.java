package com.dnd.gongmuin.chat.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.common.fixture.ChatMessageFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;

@DisplayName("[ChatMessage 통합 테스트]")
class ChatRoomControllerTest extends ApiTestSupport {

	@Autowired
	private ChatMessageRepository chatMessageRepository;

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
			.andExpect(jsonPath("$.content[0].memberId").value(chatMessages.get(0).getMemberId()))
			.andExpect(jsonPath("$.content[0].chatRoomId").value(chatMessages.get(0).getChatRoomId()))
			.andExpect(jsonPath("$.content[0].content").value(chatMessages.get(0).getContent()))
			.andExpect(jsonPath("$.content[0].type").value(chatMessages.get(0).getType().getLabel()));
	}
}