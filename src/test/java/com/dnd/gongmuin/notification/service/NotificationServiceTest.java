package com.dnd.gongmuin.notification.service;

import static com.dnd.gongmuin.notification.domain.NotificationType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.NotificationFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.dto.request.IsReadNotificationRequest;
import com.dnd.gongmuin.notification.dto.response.IsReadNotificationResponse;
import com.dnd.gongmuin.notification.repository.NotificationRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

	@Mock
	NotificationRepository notificationRepository;

	@InjectMocks
	NotificationService notificationService;

	@DisplayName("타겟 타입에 맞는 알림을 만들고 저장한다.")
	@Test
	void saveNotificationFromTarget() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		ReflectionTestUtils.setField(member1, "id", 1L);
		ReflectionTestUtils.setField(member2, "id", 2L);

		QuestionPost questionPost = QuestionPostFixture.questionPost(member1);
		ReflectionTestUtils.setField(questionPost, "id", 1L);

		// when
		notificationService.saveNotificationFromTarget("답변", questionPost.getId(), member2.getId(), member1);

		// then
		verify(notificationRepository).save(any(Notification.class));
		verify(notificationRepository).save(argThat(notification ->
			notification.getType().equals(ANSWER) &&
				notification.getTargetId().equals(questionPost.getId()) &&
				notification.getTriggerMemberId().equals(member2.getId()) &&
				notification.getMember().equals(member1)
		));
	}

	@DisplayName("알림의 안읽음 상태를 읽음 상태로 변경한다.")
	@Test
	void isReadNotification() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();

		QuestionPost questionPost = QuestionPostFixture.questionPost(member1);
		Notification notification = NotificationFixture.notification(
			ANSWER,
			questionPost.getId(),
			member2.getId(),
			member1
		);
		IsReadNotificationRequest request = new IsReadNotificationRequest(1L);

		given(notificationRepository.findById(anyLong())).willReturn(Optional.ofNullable(notification));

		// when
		IsReadNotificationResponse response = notificationService.isReadNotification(request, member1);

		// then
		assertAll(
			() -> assertThat(response.notificationId()).isEqualTo(notification.getId()),
			() -> assertThat(response.isRead()).isTrue()
		);
	}

	@DisplayName("요청 회원이 특정 알림의 소유주가 아니라면 예외가 발생한다.")
	@Test
	void isReadNotificationThrowsExceptionWhenMemberIsNotOwner() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();

		QuestionPost questionPost = QuestionPostFixture.questionPost(member1);
		Notification notification = NotificationFixture.notification(
			ANSWER,
			questionPost.getId(),
			member2.getId(),
			member1
		);
		notification.updateIsRead();
		IsReadNotificationRequest request = new IsReadNotificationRequest(1L);

		given(notificationRepository.findById(anyLong())).willReturn(Optional.ofNullable(notification));

		// when		// then
		assertThrows(ValidationException.class, () -> notificationService.isReadNotification(request, member2));
	}

	@DisplayName("읽었던 알림의 읽음 상태 변화를 하면 예외가 발생한다.")
	@Test
	void isReadNotificationThrowsExceptionWhenAlreadyRead() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();

		QuestionPost questionPost = QuestionPostFixture.questionPost(member1);
		Notification notification = NotificationFixture.notification(
			ANSWER,
			questionPost.getId(),
			member2.getId(),
			member1
		);
		notification.updateIsRead();
		IsReadNotificationRequest request = new IsReadNotificationRequest(1L);

		given(notificationRepository.findById(anyLong())).willReturn(Optional.ofNullable(notification));

		// when		// then
		assertThrows(ValidationException.class, () -> notificationService.isReadNotification(request, member1));
	}
}
