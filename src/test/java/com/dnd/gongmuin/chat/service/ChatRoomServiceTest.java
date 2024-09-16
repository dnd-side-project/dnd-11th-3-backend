package com.dnd.gongmuin.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import com.dnd.gongmuin.chat.dto.response.RejectChatResponse;
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
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[채팅방 메시지 서비스 단위 테스트]")
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

	private final PageRequest pageRequest = PageRequest.of(0, 5);
	private static final int CHAT_REWARD = 2000;

	@Mock
	private ChatMessageRepository chatMessageRepository;

	@Mock
	private ChatRoomRepository chatRoomRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private QuestionPostRepository questionPostRepository;

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
		ChatRoomDetailResponse response = chatRoomService.createChatRoom(request, inquirer);

		//then
		assertAll(
			() -> assertThat(response.questionPostId()).isEqualTo(request.questionPostId()),
			() -> assertThat(response.receiverInfo().memberId()).isEqualTo(request.answererId())
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
				.isEqualTo(previousCredit+CHAT_REWARD)
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
}