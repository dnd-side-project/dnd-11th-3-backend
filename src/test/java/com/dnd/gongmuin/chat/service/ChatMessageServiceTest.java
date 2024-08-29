package com.dnd.gongmuin.chat.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.chat.domain.ChatMessage;
import com.dnd.gongmuin.chat.dto.ChatMessageResponse;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.common.fixture.ChatMessageFixture;

@DisplayName("[채팅방 메시지 서비스 단위 테스트]")
@ExtendWith(MockitoExtension.class)
class ChatMessageServiceTest {

	private final PageRequest pageRequest = PageRequest.of(0, 5);

	@Mock
	private ChatMessageRepository chatMessageRepository;

	@InjectMocks
	private ChatMessageService chatMessageService;

	@DisplayName("[채팅방 아이디로 채팅방 메시지를 조회할 수 있다.]")
	@Test
	void getChatMessages() {
		//given
		ChatMessage chatMessage = ChatMessageFixture.chatMessage();
		given(chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(1L, pageRequest))
			.willReturn(new SliceImpl<>(List.of(chatMessage)));

		//when
		List<ChatMessageResponse> response = chatMessageService.getChatMessage(1L, pageRequest).content();

		//then
		assertAll(
			() -> assertThat(response.get(0).chatRoomId()).isEqualTo(1L),
			() -> assertThat(response).hasSize(1)
		);
	}
}