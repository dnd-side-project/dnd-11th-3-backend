package com.dnd.gongmuin.chat_inquiry.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryResponse;
import com.dnd.gongmuin.common.fixture.ChatInquiryFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[ChatInquiryRepository 테스트]")
class ChatInquiryRepositoryTest extends DataJpaTestSupport {

	private final PageRequest pageRequest = PageRequest.of(0, 10);
	private final String chatMessage = "와우";

	@Autowired
	ChatInquiryRepository chatInquiryRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	QuestionPostRepository questionPostRepository;

	@Autowired
	CreditHistoryRepository creditHistoryRepository;

	@DisplayName("회원의 채팅 요청 목록을 조회할 수 있다.")
	@Test
	void getChatInquiresByMember() {
		//given
		Member questioner = memberRepository.save(MemberFixture.member());
		Member target = memberRepository.save(MemberFixture.member());
		Member answerer = memberRepository.save(MemberFixture.member());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(questioner));
		List<ChatInquiry> chatInquiries = chatInquiryRepository.saveAll(List.of(
			chatInquiryRepository.save(ChatInquiryFixture.chatInquiry(questionPost, target, answerer, chatMessage)),
			chatInquiryRepository.save(ChatInquiryFixture.chatInquiry(questionPost, questioner, target, chatMessage)),
			chatInquiryRepository.save(ChatInquiryFixture.chatInquiry(questionPost, questioner, answerer, chatMessage))
		));

		//when
		List<ChatInquiryResponse> responses = chatInquiryRepository.getChatInquiresByMember(target, pageRequest)
			.getContent();

		//then
		Assertions.assertAll(
			() -> assertThat(responses).hasSize(2),
			() -> assertThat(responses.get(0).chatInquiryId()).isEqualTo(chatInquiries.get(1).getId()),
			() -> assertThat(responses.get(0).chatPartner().memberId()).isEqualTo(questioner.getId()),
			() -> assertThat(responses.get(1).chatInquiryId()).isEqualTo(chatInquiries.get(0).getId()),
			() -> assertThat(responses.get(1).chatPartner().memberId()).isEqualTo(answerer.getId())
		);
	}

	@DisplayName("요청중인 채팅방이 일주일이 지나면, 채팅방 상태를 거절함으로 바꾼다.")
	@Test
	void updateChatInquiryStatusRejected() {
		//given
		Member questioner = memberRepository.save(MemberFixture.member());
		Member answerer = memberRepository.save(MemberFixture.member());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(questioner));

		List<ChatInquiry> chatInquiries = chatInquiryRepository.saveAll(List.of(
			chatInquiryRepository.save(ChatInquiryFixture.chatInquiry(questionPost, questioner, answerer, chatMessage)),
			chatInquiryRepository.save(ChatInquiryFixture.chatInquiry(questionPost, questioner, answerer, chatMessage))
		));
		ReflectionTestUtils.setField(chatInquiries.get(0), "createdAt", LocalDateTime.now().minusWeeks(1));

		//when
		chatInquiryRepository.updateChatInquiryStatusRejected(LocalDateTime.now());

		em.flush();
		em.clear();

		//then
		ChatInquiry chatInquiry1 = chatInquiryRepository.findById(chatInquiries.get(0).getId()).orElseThrow();
		ChatInquiry chatInquiry2 = chatInquiryRepository.findById(chatInquiries.get(1).getId()).orElseThrow();
		assertAll(
			() -> assertThat(chatInquiry1.getStatus()).isEqualTo(InquiryStatus.REJECTED),
			() -> assertThat(chatInquiry2.getStatus()).isEqualTo(InquiryStatus.PENDING)
		);
	}
}