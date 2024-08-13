package com.dnd.gongmuin.common.fixture;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionPostFixture {

	public static QuestionPost questionPost(Member member) {
		return QuestionPost.of(
			"제목",
			"내용",
			1000,
			JobGroup.from("공업"),
			List.of(
				QuestionPostImage.from("image1.jpg"),
				QuestionPostImage.from("image2.jpg")
			),
			member
		);
	}

	public static QuestionPost questionPost(String jobGroup, Member member) {
		return QuestionPost.of(
			"제목",
			"내용",
			1000,
			JobGroup.from(jobGroup),
			List.of(
				QuestionPostImage.from("image1.jpg"),
				QuestionPostImage.from("image2.jpg")
			),
			member
		);
	}

	public static QuestionPost questionPost(Member member, String title) {
		return QuestionPost.of(
			title,
			"내용",
			1000,
			JobGroup.from("공업"),
			List.of(
				QuestionPostImage.from("image1.jpg"),
				QuestionPostImage.from("image2.jpg")
			),
			member
		);
	}

	// 단위 테스트용
	public static QuestionPost questionPost(Long questionPostId) {
		QuestionPost questionPost = QuestionPost.of(
			"제목",
			"내용",
			1000,
			JobGroup.from("공업"),
			List.of(
				QuestionPostImage.from("image1.jpg"),
				QuestionPostImage.from("image2.jpg")
			),
			MemberFixture.member(1L)
		);
		ReflectionTestUtils.setField(questionPost, "id", questionPostId);
		ReflectionTestUtils.setField(questionPost, "createdAt", LocalDateTime.now());

		return questionPost;
	}

	public static QuestionPost questionPost(Long questionPostId, Member member) {
		QuestionPost questionPost = QuestionPost.of(
			"제목",
			"내용",
			1000,
			JobGroup.from("공업"),
			List.of(
				QuestionPostImage.from("image1.jpg"),
				QuestionPostImage.from("image2.jpg")
			),
			member
		);
		ReflectionTestUtils.setField(questionPost, "id", questionPostId);
		ReflectionTestUtils.setField(questionPost, "createdAt", LocalDateTime.now());

		return questionPost;
	}
}
