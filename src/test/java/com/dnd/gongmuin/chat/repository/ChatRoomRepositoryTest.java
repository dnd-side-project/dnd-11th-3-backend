package com.dnd.gongmuin.chat.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.domain.ChatStatus;
import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.common.fixture.ChatRoomFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[ChatRoomRepository 테스트]")
class ChatRoomRepositoryTest extends DataJpaTestSupport {
	@Autowired
	ChatRoomRepository chatRoomRepository;
	@Autowired
	MemberRepository memberRepository;
	@Autowired
	QuestionPostRepository questionPostRepository;

	@DisplayName("회원이 속한 채팅방을 모두 조회할 수 있다.")
	@Test
	void getChatRoomsByMember() {
		//given
		Member questioner = memberRepository.save(MemberFixture.member());
		Member target = memberRepository.save(MemberFixture.member());
		Member answerer = memberRepository.save(MemberFixture.member());
		QuestionPost questionPost = questionPostRepository.save(QuestionPostFixture.questionPost(questioner));
		List<ChatRoom> chatRooms = chatRoomRepository.saveAll(List.of(
			chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPost, questioner, answerer)),
			chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPost, questioner, target)),
			chatRoomRepository.save(ChatRoomFixture.chatRoom(questionPost, target, answerer))
		));
		//when
		List<ChatRoomInfo> chatRoomInfos = chatRoomRepository.getChatRoomsByMember(target, ChatStatus.PENDING);
		//then
		Assertions.assertAll(
			() -> assertThat(chatRoomInfos).hasSize(2),
			() -> assertThat(chatRoomInfos.get(0).chatRoomId()).isEqualTo(chatRooms.get(1).getId()),
			() -> assertThat(chatRoomInfos.get(0).partnerId()).isEqualTo(questioner.getId()),
			() -> assertThat(chatRoomInfos.get(1).chatRoomId()).isEqualTo(chatRooms.get(2).getId()),
			() -> assertThat(chatRoomInfos.get(1).partnerId()).isEqualTo(answerer.getId())
		);
	}
}