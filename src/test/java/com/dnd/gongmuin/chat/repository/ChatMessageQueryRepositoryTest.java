package com.dnd.gongmuin.chat.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dnd.gongmuin.chat.dto.response.LatestChatMessage;
import com.dnd.gongmuin.common.fixture.ChatMessageFixture;
import com.dnd.gongmuin.common.support.TestContainerSupport;

@SpringBootTest
class ChatMessageQueryRepositoryTest extends TestContainerSupport {

	@Autowired
	ChatMessageQueryRepository chatMessageQueryRepository;
	@Autowired
	ChatMessageRepository chatMessageRepository;

	@DisplayName("채팅방 아이디 리스트로 각 채팅방의 최근 채팅 메시지를 가져올 수 있다.")
	@Test
	void findLatestChatByChatRoomIds() {
		//given
		chatMessageRepository.saveAll(List.of(
			ChatMessageFixture.chatMessage(1L, "첫번째 채팅방 첫번째 메시지", LocalDateTime.now()),
			ChatMessageFixture.chatMessage(1L, "첫번째 채팅방 두번째 메시지", LocalDateTime.now().plusHours(11)),
			ChatMessageFixture.chatMessage(2L, "두번째 채팅방 첫번째 메시지", LocalDateTime.now()),
			ChatMessageFixture.chatMessage(2L, "두번째 채팅방 두번째 메시지", LocalDateTime.now().plusHours(22)),
			ChatMessageFixture.chatMessage(3L, "세번째 채팅방 첫번째 메시지", LocalDateTime.now())
		));

		//when
		List<LatestChatMessage> responses = chatMessageQueryRepository.findLatestChatByChatRoomIds(
			List.of(1L, 2L));

		//then
		Assertions.assertAll(
			() -> assertThat(responses).hasSize(2),
			() -> assertThat(responses.stream().map(LatestChatMessage::content).collect(Collectors.toList()))
				.containsExactlyInAnyOrder("두번째 채팅방 두번째 메시지", "첫번째 채팅방 두번째 메시지") // 순서 보장x
		);
	}
}