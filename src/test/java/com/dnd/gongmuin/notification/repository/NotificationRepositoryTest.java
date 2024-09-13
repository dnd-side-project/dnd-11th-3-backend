package com.dnd.gongmuin.notification.repository;

import static com.dnd.gongmuin.notification.domain.NotificationType.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.NotificationFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.dto.response.NotificationResponse;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

class NotificationRepositoryTest extends DataJpaTestSupport {

	private final PageRequest pageRequest = PageRequest.of(0, 10);

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	QuestionPostRepository questionPostRepository;

	@Autowired
	AnswerRepository answerRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@DisplayName("회원의 알림 전체 목록을 불러온다.")
	@Test
	void getNotificationsByMember() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		Member member3 = MemberFixture.member3();
		memberRepository.saveAll(List.of(member1, member2, member3));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		questionPostRepository.save(questionPost1);

		Notification notification1 = NotificationFixture.notification(
			ANSWER, questionPost1.getId(), member2.getId(), member1
		);
		Notification notification2 = NotificationFixture.notification(
			ANSWER, questionPost1.getId(), member3.getId(), member1
		);
		Notification notification3 = NotificationFixture.notification(
			CHOSEN, questionPost1.getId(), member3.getId(), member1
		);
		notificationRepository.saveAll(List.of(notification1, notification2, notification3));

		// when
		Slice<NotificationResponse> notificationsByMember = notificationRepository.getNotificationsByMember("전체",
			member1, pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(notificationsByMember).hasSize(3),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::type)
				.containsExactly(
					"채택",
					"답변",
					"답변"
				),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::triggerMemberId)
				.containsExactly(
					member3.getId(),
					member3.getId(),
					member2.getId()
				),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::triggerMemberNickName)
				.containsExactly(
					member3.getNickname(),
					member3.getNickname(),
					member2.getNickname()
				),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::targetMemberId)
				.containsExactly(
					member1.getId(),
					member1.getId(),
					member1.getId()
				)
		);
	}

	@DisplayName("회원의 알림 채택 목록을 불러온다.")
	@Test
	void getNotificationsByMemberWithChosen() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		Member member3 = MemberFixture.member3();
		memberRepository.saveAll(List.of(member1, member2, member3));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		questionPostRepository.save(questionPost1);

		Notification notification1 = NotificationFixture.notification(
			ANSWER, questionPost1.getId(), member2.getId(), member1
		);
		Notification notification2 = NotificationFixture.notification(
			ANSWER, questionPost1.getId(), member3.getId(), member1
		);
		Notification notification3 = NotificationFixture.notification(
			CHOSEN, questionPost1.getId(), member3.getId(), member1
		);
		notificationRepository.saveAll(List.of(notification1, notification2, notification3));

		// when
		Slice<NotificationResponse> notificationsByMember = notificationRepository.getNotificationsByMember("채택",
			member1, pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(notificationsByMember).hasSize(1),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::type)
				.containsExactly(
					"채택"
				),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::triggerMemberId)
				.containsExactly(
					member3.getId()
				),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::triggerMemberNickName)
				.containsExactly(
					member3.getNickname()
				),
			() -> assertThat(notificationsByMember).extracting(NotificationResponse::targetMemberId)
				.containsExactly(
					member1.getId()
				)
		);
	}
}
