package com.dnd.gongmuin.chat_inquiry.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.chat_inquiry.dto.AcceptChatResponse;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryRequest;
import com.dnd.gongmuin.chat_inquiry.dto.CreateChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.RejectChatResponse;
import com.dnd.gongmuin.chat_inquiry.repository.ChatInquiryRepository;
import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.chatroom.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.common.fixture.ChatInquiryFixture;
import com.dnd.gongmuin.common.fixture.ChatRoomFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.service.CreditHistoryService;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.notification.dto.NotificationEvent;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[채팅 요청 서비스 단위 테스트]")
@ExtendWith(MockitoExtension.class)
class ChatInquiryServiceTest {

	private static final int CHAT_REWARD = 2000;
	private static final String CHAT_MESSAGE = "와";
	private final PageRequest pageRequest = PageRequest.of(0, 5);

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@Mock
	private ChatInquiryRepository chatInquiryRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private QuestionPostRepository questionPostRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private CreditHistoryService creditHistoryService;

	@InjectMocks
	private ChatInquiryService chatInquiryService;

	@DisplayName("[댓글 작성자에게 채팅을 요청할 수 있다.]")
	@Test
	void createInquiry() {
		//given
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		CreateChatInquiryRequest request = new CreateChatInquiryRequest(
			questionPost.getId(),
			answerer.getId(),
			CHAT_MESSAGE
		);

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(memberRepository.findById(answerer.getId()))
			.willReturn(Optional.of(answerer));

		CreateChatInquiryResponse response = chatInquiryService.createChatInquiry(request, inquirer);
		System.out.println("response = " + response);
		// //then
		// assertAll(
		//
		// );

	}

	@DisplayName("[요청자의 크레딧이 2000미만이면 채팅을 요청할 수 없다.]")
	@Test
	void createInquiry_fails() {
		//given
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		ReflectionTestUtils.setField(inquirer, "credit", CHAT_REWARD - 1);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		CreateChatInquiryRequest request = new CreateChatInquiryRequest(
			questionPost.getId(),
			answerer.getId(),
			CHAT_MESSAGE
		);

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(memberRepository.findById(answerer.getId()))
			.willReturn(Optional.of(answerer));

		//when & then
		assertThatThrownBy(() -> chatInquiryService.createChatInquiry(request, inquirer))
			.isInstanceOf(ValidationException.class)
			.hasMessageContaining(MemberErrorCode.NOT_ENOUGH_CREDIT.getMessage());
	}

	@DisplayName("[회원이 속한 채팅 요청 목록을 조회할 수 있다.]")
	@Test
	void getChatInquiresByMember() {
		//given
		Long chatInquiryId = 1L;
		Member targetMember = MemberFixture.member(1L);
		Member partner = MemberFixture.member(2L);
		ChatInquiryResponse chatInquiryResponse = new ChatInquiryResponse(
			chatInquiryId, CHAT_MESSAGE, InquiryStatus.PENDING, true, partner.getId(),
			partner.getNickname(), partner.getJobGroup(), partner.getProfileImageNo()
		);
		given(chatInquiryRepository.getChatInquiresByMember(targetMember, pageRequest))
			.willReturn(new SliceImpl<>(List.of(chatInquiryResponse), pageRequest, false));

		//when
		List<ChatInquiryResponse> response = chatInquiryService.getChatInquiresByMember(
			targetMember, pageRequest).content();

		//then
		assertAll(
			() -> assertThat(response).hasSize(1),
			() -> assertThat(response.get(0).chatInquiryId())
				.isEqualTo(chatInquiryId),
			() -> assertThat(response.get(0).partnerInfo().memberId())
				.isEqualTo(partner.getId()),
			() -> assertThat(response.get(0).message())
				.isEqualTo(CHAT_MESSAGE)
		);
	}

	@DisplayName("[답변자가 채팅 요청을 수락할 수 있다.]")
	@Test
	void acceptChat() {
		//given
		Long chatInquiryId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		int previousCredit = answerer.getCredit();
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatInquiry chatInquiry = ChatInquiryFixture.chatInquiry(questionPost, inquirer, answerer, CHAT_MESSAGE);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(1L, questionPost, inquirer, answerer);
		given(chatInquiryRepository.findById(chatInquiryId))
			.willReturn(Optional.of(chatInquiry));
		given(chatRoomRepository.save(any(ChatRoom.class)))
			.willReturn(chatRoom);

		//when
		AcceptChatResponse response = chatInquiryService.acceptChat(chatInquiryId, answerer);

		//then
		assertAll(
			() -> assertThat(response.inquiryStatus())
				.isEqualTo(InquiryStatus.ACCEPTED.getLabel()),
			() -> assertThat(response.credit())
				.isEqualTo(previousCredit + CHAT_REWARD)
		);
	}

	@DisplayName("[답변자가 채팅 요청을 수락할 때 채팅 수락 알림이 발행된다.]")
	@Test
	void acceptChatWithEventPublish() {
		//given
		Long chatInquiryId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		int previousCredit = answerer.getCredit();
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatInquiry chatInquiry = ChatInquiryFixture.chatInquiry(questionPost, inquirer, answerer, CHAT_MESSAGE);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(1L, questionPost, inquirer, answerer);
		given(chatInquiryRepository.findById(chatInquiryId))
			.willReturn(Optional.of(chatInquiry));
		given(chatRoomRepository.save(any(ChatRoom.class)))
			.willReturn(chatRoom);

		//when
		AcceptChatResponse response = chatInquiryService.acceptChat(chatInquiryId, answerer);

		//then
		assertAll(
			() -> assertThat(response.inquiryStatus())
				.isEqualTo(InquiryStatus.ACCEPTED.getLabel()),
			() -> assertThat(response.credit())
				.isEqualTo(previousCredit + CHAT_REWARD),
			() -> verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class))
		);
	}

	@DisplayName("[답변자가 채팅 요청을 거절할 수 있다.]")
	@Test
	void rejectChat() {
		//given
		Long chatInquiryId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatInquiry chatInquiry = ChatInquiryFixture.chatInquiry(questionPost, inquirer, answerer, CHAT_MESSAGE);

		given(chatInquiryRepository.findById(chatInquiryId))
			.willReturn(Optional.of(chatInquiry));

		//when
		RejectChatResponse response = chatInquiryService.rejectChat(chatInquiryId, answerer);

		//then
		assertThat(response.inquiryStatus())
			.isEqualTo(InquiryStatus.REJECTED.getLabel());
	}

	@DisplayName("[답변자가 채팅 요청을 거절할 때 채팅 거절 알림이 발행된다.]")
	@Test
	void rejectChatWithEventPublish() {
		//given
		Long chatInquiryId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatInquiry chatInquiry = ChatInquiryFixture.chatInquiry(questionPost, inquirer, answerer, CHAT_MESSAGE);

		given(chatInquiryRepository.findById(chatInquiryId))
			.willReturn(Optional.of(chatInquiry));

		//when
		RejectChatResponse response = chatInquiryService.rejectChat(chatInquiryId, answerer);

		//then
		assertAll(
			() -> assertThat(response.inquiryStatus()).isEqualTo(InquiryStatus.REJECTED.getLabel()),
			() -> verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class))
		);
	}

	@DisplayName("일주일이 지난 요청에 경우 자동으로 거절하고, 요청자에게 크레딧을 반환한다.")
	@Test
	void rejectChatAuto() {
		// given
		List<Long> rejectedInquirerIds = List.of(1L, 2L);
		given(chatInquiryRepository.getAutoRejectedInquirerIds())
			.willReturn(rejectedInquirerIds);

		// when
		chatInquiryService.rejectChatAuto();

		// then
		verify(chatInquiryRepository).getAutoRejectedInquirerIds();
		verify(chatInquiryRepository).updateChatInquiryStatusRejected();
		verify(memberRepository).refundInMemberIds(rejectedInquirerIds, CHAT_REWARD);
		verify(creditHistoryService).saveCreditHistoryInMemberIds(
			rejectedInquirerIds, CreditType.CHAT_REFUND, CHAT_REWARD
		);
	}
}