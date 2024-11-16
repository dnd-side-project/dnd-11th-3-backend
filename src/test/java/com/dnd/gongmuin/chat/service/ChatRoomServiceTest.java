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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.chatroom.domain.ChatMessage;
import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.chatroom.dto.response.ChatMessageResponse;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chatroom.dto.response.LatestChatMessage;
import com.dnd.gongmuin.chatroom.exception.ChatErrorCode;
import com.dnd.gongmuin.chatroom.repository.ChatMessageQueryRepository;
import com.dnd.gongmuin.chatroom.repository.ChatMessageRepository;
import com.dnd.gongmuin.chatroom.repository.ChatRoomRepository;
import com.dnd.gongmuin.chatroom.service.ChatRoomService;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.common.fixture.ChatMessageFixture;
import com.dnd.gongmuin.common.fixture.ChatRoomFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

@DisplayName("[채팅방 서비스 단위 테스트]")
@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

	private final PageRequest pageRequest = PageRequest.of(0, 5);
	@Mock
	private ChatMessageRepository chatMessageRepository;

	@Mock
	private ChatMessageQueryRepository chatMessageQueryRepository;

	@Mock
	private ChatRoomRepository chatRoomRepository;

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

	@DisplayName("[회원이 속한 채팅방 목록을 조회할 수 있다.]")
	@Test
	void getChatRoomsByMember() {
		//given
		Long chatRoomId = 1L;
		Member targetMember = MemberFixture.member(1L);
		Member partner = MemberFixture.member(2L);
		ChatRoomInfo chatRoomInfo = new ChatRoomInfo(
			chatRoomId, partner.getId(),
			partner.getNickname(), partner.getJobGroup(), partner.getProfileImageNo()
		);
		LatestChatMessage latestChatMessage = new LatestChatMessage(
			chatRoomId, "와", "텍스트", LocalDateTime.now()
		);

		given(chatRoomRepository.getChatRoomsByMember(targetMember, pageRequest))
			.willReturn(new SliceImpl<>(List.of(chatRoomInfo), pageRequest, false));
		given(chatMessageQueryRepository.findLatestChatByChatRoomIds(List.of(chatRoomId)))
			.willReturn(List.of(latestChatMessage));

		//when
		List<ChatRoomSimpleResponse> response = chatRoomService.getChatRoomsByMember(
			targetMember, pageRequest).content();

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
			() -> assertThat(response.chatPartner().memberId())
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
			() -> assertThat(response.chatPartner().memberId())
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
}