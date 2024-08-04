package com.dnd.gongmuin.common.fixture;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class QuestionPostFixture {

	public static QuestionPost questionPost(Member member) {
		return QuestionPost.of(
			"제목",
			"내용",
			1000,
			JobGroup.of("공업"),
			List.of(
				QuestionPostImage.from("image1.jpg"),
				QuestionPostImage.from("image2.jpg")
			),
			member
		);
	}

	// 단위 테스트용
	public static QuestionPost questionPost(Long id) {
		QuestionPost questionPost = QuestionPost.of(
			"제목",
			"내용",
			1000,
			JobGroup.of("공업"),
			List.of(
				QuestionPostImage.from("image1.jpg"),
				QuestionPostImage.from("image2.jpg")
			),
			MemberFixture.member()
		);
		ReflectionTestUtils.setField(questionPost, "id", id);
		ReflectionTestUtils.setField(questionPost, "createdAt", LocalDateTime.now());

		return questionPost;
	}
}
