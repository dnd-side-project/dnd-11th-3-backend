package com.dnd.gongmuin.notification.controller;

import static com.dnd.gongmuin.notification.domain.NotificationType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.NotificationFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.ApiTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.notification.domain.Notification;
import com.dnd.gongmuin.notification.repository.NotificationRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@DisplayName("[NotificationController] 통합테스트")
class NotificationControllerTest extends ApiTestSupport {

	private final PageRequest pageRequest = PageRequest.of(0, 10);

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	QuestionPostRepository questionPostRepository;

	@Autowired
	AnswerRepository answerRepository;

	@Autowired
	NotificationRepository notificationRepository;

	@AfterEach
	void tearDown() {
		answerRepository.deleteAll();
		notificationRepository.deleteAll();
		questionPostRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@DisplayName("로그인 된 회원의 알림 목록을 조회힌다.")
	@Test
	void test() throws Exception {
		// given
		Member member2 = MemberFixture.member2();
		Member member3 = MemberFixture.member3();
		memberRepository.saveAll(List.of(member2, member3));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(loginMember, "첫 번째 게시글입니다.");
		questionPostRepository.save(questionPost1);

		Notification notification1 = NotificationFixture.notification(
			ANSWER, questionPost1.getId(), member2.getId(), loginMember
		);
		Notification notification2 = NotificationFixture.notification(
			ANSWER, questionPost1.getId(), member3.getId(), loginMember
		);
		Notification notification3 = NotificationFixture.notification(
			CHOSEN, questionPost1.getId(), member3.getId(), loginMember
		);
		notificationRepository.saveAll(List.of(notification1, notification2, notification3));

		// when		// then
		mockMvc.perform(get("/api/notifications")
				.param("type", "전체")
				.cookie(accessToken)
			)
			.andExpect(status().isOk())
			.andDo(MockMvcResultHandlers.print())
			.andExpect(jsonPath("$.size").value(3))
			.andExpect(jsonPath("$.content[0].type").value(CHOSEN.getLabel()))
			.andExpect(jsonPath("$.content[1].type").value(ANSWER.getLabel()))
			.andExpect(jsonPath("$.content[2].type").value(ANSWER.getLabel()))
			.andExpect(jsonPath("$.content[0].triggerMemberId").value(member3.getId()))
			.andExpect(jsonPath("$.content[1].triggerMemberId").value(member3.getId()))
			.andExpect(jsonPath("$.content[2].triggerMemberId").value(member2.getId()))
			.andExpect(jsonPath("$.content[0].targetMemberId").value(loginMember.getId()))
			.andExpect(jsonPath("$.content[1].targetMemberId").value(loginMember.getId()))
			.andExpect(jsonPath("$.content[2].targetMemberId").value(loginMember.getId()));
	}
}
