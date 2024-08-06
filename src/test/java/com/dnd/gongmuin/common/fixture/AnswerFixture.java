package com.dnd.gongmuin.common.fixture;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.answer.domain.Answer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerFixture {

	// 단위 테스트용
	public static Answer answer(Long answerId, Long questionPostId){
		Answer answer = Answer.of(
			"답변 내용",
			false,
			questionPostId,
			MemberFixture.member(1L)
		);

		ReflectionTestUtils.setField(answer, "id", answerId);
		ReflectionTestUtils.setField(answer, "createdAt", LocalDateTime.now());

		return answer;
	}
}
