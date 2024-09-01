package com.dnd.gongmuin.notification.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.domain.NotificationType;
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
			notification.getType().equals(NotificationType.ANSWER) &&
				notification.getTargetId().equals(questionPost.getId()) &&
				notification.getTriggerMemberId().equals(member2.getId()) &&
				notification.getMember().equals(member1)
		));

	}
}
