package com.dnd.gongmuin.chat_inquiry.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryRequest;
import com.dnd.gongmuin.chat_inquiry.repository.ChatInquiryRepository;
import com.dnd.gongmuin.chatroom.repository.ChatMessageRepository;
import com.dnd.gongmuin.chatroom.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.fixture.ChatInquiryFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[채팅 요청 통합 테스트]")
class ChatInquiryControllerTest extends ApiTestSupport {

	private static final int CHAT_REWARD = 2000;
	private static final String INQUIRY_MESSAGE = "와";

	@Autowired
	private ChatMessageRepository chatMessageRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private ChatInquiryRepository chatInquiryRepository;

	@Autowired
	private CreditHistoryRepository creditHistoryRepository;

	@AfterEach
	void teardown() {
		creditHistoryRepository.deleteAll();
		memberRepository.deleteAll();
		questionPostRepository.deleteAll();
		chatInquiryRepository.deleteAll();
		chatRoomRepository.deleteAll();
		chatMessageRepository.deleteAll();
	}

	@DisplayName("[답변자 아이디로 채팅을 요청할 수 있다.]")
	@Test
	void createChatInquiry() throws Exception {
		//given
		int previousCredit = loginMember.getCredit();
		Member answerer = memberRepository.save(MemberFixture.member5());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		CreateChatInquiryRequest request = new CreateChatInquiryRequest(
			questionPost.getId(),
			answerer.getId(),
			INQUIRY_MESSAGE
		);
		//when & then
		mockMvc.perform(post("/api/chat/inquiries")
				.cookie(accessToken)
				.content(toJson(request))
				.contentType(APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.inquiryStatus").value(InquiryStatus.PENDING.getLabel()))
			.andExpect(jsonPath("$.credit").value(previousCredit-CHAT_REWARD))
			.andDo(MockMvcResultHandlers.print());
	}

	@DisplayName("[채팅 요청 아이디로 상세 채팅 요청을 조회할 수 있다.]")
	@Test
	void getChatInquiryById() throws Exception {
		//given
		Member chatPartner = memberRepository.save(MemberFixture.member5());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(loginMember));
		ChatInquiry chatInquiry = chatInquiryRepository.save(
			ChatInquiryFixture.chatInquiry(questionPost, loginMember, chatPartner, INQUIRY_MESSAGE)
		);

		//when & then
		mockMvc.perform(get("/api/chat/inquiries/{chatInquiryId}", chatInquiry.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.chatInquiryId")
				.value(chatInquiry.getId()))
			.andExpect(jsonPath("$.inquiryStatus")
				.value(InquiryStatus.PENDING.getLabel()))
			.andExpect(jsonPath("$.chatPartner.memberId")
				.value(chatPartner.getId()))
			.andExpect(jsonPath("$.isInquirer")
				.value(chatInquiry.getInquirer().equals(loginMember)))
			.andExpect(jsonPath("$.inquiryStatus")
				.value(InquiryStatus.PENDING.getLabel()));
	}

	@DisplayName("[회원의 채팅 요청 목록을 조회할 수 있다.]")
	@Test
	void getChatInquiresByMember() throws Exception {
		//given
		Member member1 = memberRepository.save(MemberFixture.member4());
		Member member2 = memberRepository.save(MemberFixture.member5());
		List<QuestionPost> questionPosts = questionPostRepository.saveAll(
			List.of(
				questionPostRepository.save(QuestionPostFixture.questionPost(member1)),
				questionPostRepository.save(QuestionPostFixture.questionPost(member2))
			)
		);
		ChatInquiry chatInquiry1 = chatInquiryRepository.save(
			ChatInquiryFixture.chatInquiry(questionPosts.get(0), member1, loginMember, INQUIRY_MESSAGE));
		ChatInquiry chatInquiry2 = chatInquiryRepository.save(
			ChatInquiryFixture.chatInquiry(questionPosts.get(1), loginMember, member2, INQUIRY_MESSAGE));

		// when & then
		mockMvc.perform(get("/api/chat/inquiries")
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size").value(2))
			.andExpect(jsonPath("$.content[0].chatInquiryId").value(chatInquiry2.getId())) // 내림차순
			.andExpect(jsonPath("$.content[0].chatPartner.memberId").value(member2.getId()))
			.andExpect(jsonPath("$.content[0].isInquirer").value(true))
			.andExpect(jsonPath("$.content[0].inquiryStatus").value(InquiryStatus.PENDING.getLabel()))

			.andExpect(jsonPath("$.content[1].chatInquiryId").value(chatInquiry1.getId()))
			.andExpect(jsonPath("$.content[1].chatPartner.memberId").value(member1.getId()))
			.andExpect(jsonPath("$.content[1].isInquirer").value(false))
			.andExpect(jsonPath("$.content[1].inquiryStatus").value(InquiryStatus.PENDING.getLabel()))
			.andDo(MockMvcResultHandlers.print());
	}

	@DisplayName("[답변자가 채팅 요청을 수락할 수 있다.]")
	@Test
	void acceptChatRoom() throws Exception {
		Member inquirer = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(inquirer));
		ChatInquiry chatInquiry = chatInquiryRepository.save(
			ChatInquiryFixture.chatInquiry(questionPost, inquirer, loginMember, INQUIRY_MESSAGE));
		int previousAnswererCredit = loginMember.getCredit();

		mockMvc.perform(patch("/api/chat/inquiries/{chatInquiryId}/accept", chatInquiry.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.inquiryStatus").value(InquiryStatus.ACCEPTED.getLabel()))
			.andExpect(jsonPath("$.credit").value(previousAnswererCredit + CHAT_REWARD));
	}

	@DisplayName("[답변자가 채팅 요청을 거절할 수 있다.]")
	@Test
	void rejectChatRoom() throws Exception {
		Member inquirer = memberRepository.save(MemberFixture.member4());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(inquirer));
		ChatInquiry chatInquiry = chatInquiryRepository.save(
			ChatInquiryFixture.chatInquiry(questionPost, inquirer, loginMember, INQUIRY_MESSAGE));

		mockMvc.perform(patch("/api/chat/inquiries/{chatInquiryId}/reject", chatInquiry.getId())
				.cookie(accessToken))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.inquiryStatus").value(InquiryStatus.REJECTED.getLabel()));
	}
}