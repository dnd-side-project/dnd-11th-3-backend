package com.dnd.gongmuin.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
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

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.domain.ChatStatus;
import com.dnd.gongmuin.chat.dto.request.CreateChatRoomRequest;
import com.dnd.gongmuin.chat.dto.response.AcceptChatResponse;
import com.dnd.gongmuin.chat.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chat.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chat.dto.response.CreateChatRoomResponse;
import com.dnd.gongmuin.chat.dto.response.LatestChatMessage;
import com.dnd.gongmuin.chat.dto.response.RejectChatResponse;
import com.dnd.gongmuin.chat.exception.ChatErrorCode;
import com.dnd.gongmuin.chat.repository.ChatMessageQueryRepository;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.chat.repository.ChatRoomRepository;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.common.fixture.ChatMessageFixture;
import com.dnd.gongmuin.common.fixture.ChatRoomFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.notification.dto.NotificationEvent;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[채팅방 서비스 단위 테스트]")
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

	private static final int CHAT_REWARD = 2000;
	private final PageRequest pageRequest = PageRequest.of(0, 5);
	@Mock
	private ChatMessageRepository chatMessageRepository;

	@Mock
	private ChatMessageQueryRepository chatMessageQueryRepository;

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private QuestionPostRepository questionPostRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@InjectMocks
	private ChatRoomService chatRoomService;

	@DisplayName("[채팅방 아이디로 채팅방 메시지를 조회할 수 있다.]")
	@Test
	void getChatMessages() {
		//given
		ChatMessage chatMessage = ChatMessageFixture.chatMessage();
		given(chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(1L, pageRequest))
			.willReturn(new SliceImpl<>(List.of(chatMessage)));

		//when
		List<ChatMessageResponse> response = chatRoomService.getChatMessages(1L, pageRequest).content();

		//then
		assertAll(
			() -> assertThat(response).hasSize(1)
		);
	}

	@DisplayName("[요청자가 채팅방을 생성할 수 있다.]")
	@Test
	void createChatRoom() {
		//given
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		CreateChatRoomRequest request = new CreateChatRoomRequest(
			questionPost.getId(),
			answerer.getId()
		);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(memberRepository.findById(answerer.getId()))
			.willReturn(Optional.of(answerer));
		given(chatRoomRepository.save(any(ChatRoom.class)))
			.willReturn(chatRoom);

		//when
		CreateChatRoomResponse response = chatRoomService.createChatRoom(request, inquirer);

		//then
		assertAll(
			() -> assertThat(response.questionPostId()).isEqualTo(request.questionPostId()),
			() -> assertThat(response.receiverInfo().memberId()).isEqualTo(request.answererId())
		);
	}

	@DisplayName("[요청자가 채팅방을 생성 시 생성 알림이 발행된다.]")
	@Test
	void createChatRoomWithEventPublish() {
		//given
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		CreateChatRoomRequest request = new CreateChatRoomRequest(
			questionPost.getId(),
			answerer.getId()
		);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(memberRepository.findById(answerer.getId()))
			.willReturn(Optional.of(answerer));
		given(chatRoomRepository.save(any(ChatRoom.class)))
			.willReturn(chatRoom);

		//when
		CreateChatRoomResponse response = chatRoomService.createChatRoom(request, inquirer);

		//then
		assertAll(
			() -> assertThat(response.questionPostId()).isEqualTo(request.questionPostId()),
			() -> assertThat(response.receiverInfo().memberId()).isEqualTo(request.answererId()),
			() -> verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class))
		);
	}

	@DisplayName("[요청자의 크레딧이 2000미만이면 채팅방을 생성할 수 없다.]")
	@Test
	void createChatRoom_fail() {
		//given
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		ReflectionTestUtils.setField(inquirer, "credit", 1999);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		CreateChatRoomRequest request = new CreateChatRoomRequest(
			questionPost.getId(),
			answerer.getId()
		);

		given(questionPostRepository.findById(questionPost.getId()))
			.willReturn(Optional.of(questionPost));
		given(memberRepository.findById(answerer.getId()))
			.willReturn(Optional.of(answerer));

		//when & then
		assertThatThrownBy(() -> chatRoomService.createChatRoom(request, inquirer))
			.isInstanceOf(ValidationException.class)
			.hasMessageContaining(MemberErrorCode.NOT_ENOUGH_CREDIT.getMessage());
	}

	@DisplayName("[회원이 속한 수락 상태 채팅방 목록을 조회할 수 있다.]")
	@Test
	void getChatRoomsByMember() {
		//given
		Long chatRoomId = 1L;
		ChatStatus status = ChatStatus.ACCEPTED;
		Member targetMember = MemberFixture.member(1L);
		Member partner = MemberFixture.member(2L);
		ChatRoomInfo chatRoomInfo = new ChatRoomInfo(
			chatRoomId, partner.getId(), partner.getNickname(), partner.getJobGroup(), partner.getProfileImageNo()
		);
		LatestChatMessage latestChatMessage = new LatestChatMessage(
			chatRoomId, "와", "텍스트", LocalDateTime.now()
		);

		given(chatRoomRepository.getChatRoomsByMember(targetMember, status, pageRequest))
			.willReturn(new SliceImpl<>(List.of(chatRoomInfo), pageRequest, false));
		given(chatMessageQueryRepository.findLatestChatByChatRoomIds(List.of(chatRoomId)))
			.willReturn(List.of(latestChatMessage));

		//when
		List<ChatRoomSimpleResponse> response = chatRoomService.getChatRoomsByMember(
			targetMember, status.getLabel(), pageRequest).content();

		//then
		assertAll(
			() -> assertThat(response).hasSize(1),
			() -> assertThat(response.get(0).chatRoomId())
				.isEqualTo(chatRoomId),
			() -> assertThat(response.get(0).chatPartner().memberId())
				.isEqualTo(partner.getId()),
			() -> assertThat(response.get(0).latestMessage())
				.isEqualTo(latestChatMessage.content())
		);
	}

	@DisplayName("[요청자가 채팅방 아이디로 채팅방을 조회할 수 있다.]")
	@Test
	void getChatRoomById_Inquirer() {
		//given
		Long chatRoomId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(chatRoomRepository.findById(chatRoomId))
			.willReturn(Optional.of(chatRoom));

		//when
		ChatRoomDetailResponse response
			= chatRoomService.getChatRoomById(chatRoomId, inquirer);

		//then
		assertAll(
			() -> assertThat(response.questionPostId())
				.isEqualTo(questionPost.getId()),
			() -> assertThat(response.receiverInfo().memberId())
				.isEqualTo(answerer.getId())
		);
	}

	@DisplayName("[답변자가 채팅방 아이디로 채팅방을 조회할 수 있다.]")
	@Test
	void getChatRoomById_Answerer() {
		//given
		Long chatRoomId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(chatRoomRepository.findById(chatRoomId))
			.willReturn(Optional.of(chatRoom));

		//when
		ChatRoomDetailResponse response
			= chatRoomService.getChatRoomById(chatRoomId, answerer);

		//then
		assertAll(
			() -> assertThat(response.questionPostId())
				.isEqualTo(questionPost.getId()),
			() -> assertThat(response.receiverInfo().memberId())
				.isEqualTo(inquirer.getId())
		);
	}

	@DisplayName("[채팅방에 속하지 않은 사람은 채팅방을 조회할 수 없다.]")
	@Test
	void getChatRoomById_Unauthorized() {
		//given
		Long chatRoomId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		Member unrelatedMember = MemberFixture.member(3L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(chatRoomRepository.findById(chatRoomId))
			.willReturn(Optional.of(chatRoom));

		//when & then
		assertThatThrownBy(() -> chatRoomService.getChatRoomById(chatRoomId, unrelatedMember))
			.isInstanceOf(ValidationException.class)
			.hasMessageContaining(ChatErrorCode.UNAUTHORIZED_CHAT_ROOM.getMessage());
	}

	@DisplayName("[답변자가 채팅 요청을 수락할 수 있다.]")
	@Test
	void acceptChat() {
		//given
		Long chatRoomId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		int previousCredit = answerer.getCredit();
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(chatRoomRepository.findById(chatRoomId))
			.willReturn(Optional.of(chatRoom));

		//when
		AcceptChatResponse response = chatRoomService.acceptChat(chatRoomId, answerer);

		//then
		assertAll(
			() -> assertThat(response.chatStatus())
				.isEqualTo(ChatStatus.ACCEPTED.getLabel()),
			() -> assertThat(response.credit())
				.isEqualTo(previousCredit + CHAT_REWARD)
		);
	}

	@DisplayName("[답변자가 채팅 요청을 수락할 때 채팅 수락 알림이 발행된다.]")
	@Test
	void acceptChatWithEventPublish() {
		//given
		Long chatRoomId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		int previousCredit = answerer.getCredit();
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(chatRoomRepository.findById(chatRoomId))
			.willReturn(Optional.of(chatRoom));

		//when
		AcceptChatResponse response = chatRoomService.acceptChat(chatRoomId, answerer);

		//then
		assertAll(
			() -> assertThat(response.chatStatus())
				.isEqualTo(ChatStatus.ACCEPTED.getLabel()),
			() -> assertThat(response.credit())
				.isEqualTo(previousCredit + CHAT_REWARD),
			() -> verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class))
		);
	}

	@DisplayName("[답변자가 채팅 요청을 거절할 수 있다.]")
	@Test
	void rejectChat() {
		//given
		Long chatRoomId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(chatRoomRepository.findById(chatRoomId))
			.willReturn(Optional.of(chatRoom));

		//when
		RejectChatResponse response = chatRoomService.rejectChat(chatRoomId, answerer);

		//then
		assertThat(response.chatStatus())
			.isEqualTo(ChatStatus.REJECTED.getLabel());
	}

	@DisplayName("[답변자가 채팅 요청을 거절할 때 채팅 거절 알림이 발행된다.]")
	@Test
	void rejectChatWithEventPublish() {
		//given
		Long chatRoomId = 1L;
		Member inquirer = MemberFixture.member(1L);
		Member answerer = MemberFixture.member(2L);
		QuestionPost questionPost = QuestionPostFixture.questionPost(inquirer);
		ChatRoom chatRoom = ChatRoomFixture.chatRoom(questionPost, inquirer, answerer);

		given(chatRoomRepository.findById(chatRoomId))
			.willReturn(Optional.of(chatRoom));

		//when
		RejectChatResponse response = chatRoomService.rejectChat(chatRoomId, answerer);

		//then
		assertAll(
			() -> assertThat(response.chatStatus()).isEqualTo(ChatStatus.REJECTED.getLabel()),
			() -> verify(eventPublisher, times(1)).publishEvent(any(NotificationEvent.class))
		);
	}
}