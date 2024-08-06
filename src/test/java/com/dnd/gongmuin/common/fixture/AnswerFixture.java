package com.dnd.gongmuin.common.fixture;

import java.time.LocalDateTime;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.answer.domain.Answer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerFixture {

	// 단위 테스트용
	public static Answer answer(Long id){
		Answer answer = Answer.of(
			"제목",
			false,
			1L,
			MemberFixture.member()
		);

		ReflectionTestUtils.setField(answer, "id", id);
		ReflectionTestUtils.setField(answer, "createdAt", LocalDateTime.now());

		return answer;
	}
}
