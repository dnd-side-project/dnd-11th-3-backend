package com.dnd.gongmuin.chat.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

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
			.andExpect(jsonPath("$.content[0].type").value(chatMessages.get(0).getType().getLabel()))
			.andExpect(jsonPath("$.content[0].isRead").value(chatMessages.get(0).getIsRead()));
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

	@DisplayName("[회원의 채팅방 목록을 조회할 수 있다.]")
	@Test
	void getChatRoomsByMember() throws Exception {
		//given
		Member member1 = memberRepository.save(MemberFixture.member4());
		Member member2 = memberRepository.save(MemberFixture.member5());
		List<QuestionPost> questionPosts = questionPostRepository.saveAll(
			List.of(
				questionPostRepository.save(QuestionPostFixture.questionPost(member1)),
				questionPostRepository.save(QuestionPostFixture.questionPost(member2))
			)
		);
		ChatRoom chatRoom1 = chatRoomRepository.save(
			ChatRoomFixture.chatRoom(questionPosts.get(0), member1, loginMember));
		ChatRoom chatRoom2 = chatRoomRepository.save(
			ChatRoomFixture.chatRoom(questionPosts.get(0), member2, loginMember));
		ChatRoom chatRoom3 = chatRoomRepository.save(
			ChatRoomFixture.chatRoom(questionPosts.get(1), loginMember, member1));
		ChatRoom chatRoom4 = chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPosts.get(1), member2, member1));
		chatMessageRepository.saveAll(
			List.of(
				chatMessageRepository.save(
					ChatMessageFixture.chatMessage(chatRoom1.getId(), "11", LocalDateTime.now())),
				chatMessageRepository.save(
					ChatMessageFixture.chatMessage(chatRoom1.getId(), "12", LocalDateTime.now())),
				chatMessageRepository.save(
					ChatMessageFixture.chatMessage(chatRoom2.getId(), "21", LocalDateTime.now())),
				chatMessageRepository.save(
					ChatMessageFixture.chatMessage(chatRoom2.getId(), "22", LocalDateTime.now())),
				chatMessageRepository.save(
					ChatMessageFixture.chatMessage(chatRoom3.getId(), "31", LocalDateTime.now())),
				chatMessageRepository.save(
					ChatMessageFixture.chatMessage(chatRoom3.getId(), "32", LocalDateTime.now())),
				chatMessageRepository.save(
					ChatMessageFixture.chatMessage(chatRoom4.getId(), "41", LocalDateTime.now())),
				chatMessageRepository.save(ChatMessageFixture.chatMessage(chatRoom4.getId(), "42", LocalDateTime.now()))
			)
		);
		mockMvc.perform(get("/api/chat-rooms")
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print());
	}

	@DisplayName("[채팅방 아이디로 채팅방을 상세조회할 수 있다.]")
	@Test
	void getChatRoomById() throws Exception {
		Member inquirer = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(inquirer));
		ChatRoom chatRoom = chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPost, inquirer, loginMember));

		mockMvc.perform(get("/api/chat-rooms/{chatRoomId}", chatRoom.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.questionPostId").value(questionPost.getId()))
			.andExpect(jsonPath("$.targetJobGroup").value(questionPost.getJobGroup().getLabel()))
			.andExpect(jsonPath("$.title").value(questionPost.getTitle()))
			.andExpect(jsonPath("$.receiverInfo.memberId").value(inquirer.getId()))
			.andExpect(jsonPath("$.receiverInfo.nickname").value(inquirer.getNickname()))
			.andExpect(jsonPath("$.receiverInfo.memberJobGroup").value(inquirer.getJobGroup().getLabel()))
			.andExpect(jsonPath("$.receiverInfo.profileImageNo").value(inquirer.getProfileImageNo()));
	}

	@DisplayName("[답변자가 채팅 요청을 수락할 수 있다.]")
	@Test
	void acceptChatRoom() throws Exception {
		Member inquirer = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(inquirer));
		ChatRoom chatRoom = chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPost, inquirer, loginMember));
		int previousAnswererCredit = chatRoom.getAnswerer().getCredit();

		mockMvc.perform(patch("/api/chat-rooms/{chatRoomId}/accept", chatRoom.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chatStatus").value(ChatStatus.ACCEPTED.getLabel()))
			.andExpect(jsonPath("$.credit").value(previousAnswererCredit + CHAT_REWARD));
	}

	@DisplayName("[답변자가 채팅 요청을 거절할 수 있다.]")
	@Test
	void rejectChatRoom() throws Exception {
		Member inquirer = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(inquirer));
		ChatRoom chatRoom = chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPost, inquirer, loginMember));

		mockMvc.perform(patch("/api/chat-rooms/{chatRoomId}/reject", chatRoom.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chatStatus").value(ChatStatus.REJECTED.getLabel()));
	}
}