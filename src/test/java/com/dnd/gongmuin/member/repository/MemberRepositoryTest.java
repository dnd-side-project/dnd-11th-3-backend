package com.dnd.gongmuin.member.repository;

import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.answer.repository.AnswerRepository;
import com.dnd.gongmuin.common.fixture.AnswerFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsByMemberResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsByMemberResponse;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

class MemberRepositoryTest extends DataJpaTestSupport {

	private final PageRequest pageRequest = PageRequest.of(0, 10);

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	QuestionPostRepository questionPostRepository;

	@Autowired
	AnswerRepository answerRepository;

	@DisplayName("소셜이메일로 특정 회원을 조회한다.")
	@Test
	void findMemberBySocialEmail() {
		// given
		Member 공무인1 = createMember("공무인1", "영태", "kakao1234/gongmuin@nate.com", "gongumin@korea.kr");
		Member savedMember = memberRepository.save(공무인1);

		// when
		Member findMember = memberRepository.findBySocialEmail("kakao1234/gongmuin@nate.com").get();

		// then
		assertThat(findMember.getNickname()).isEqualTo("공무인1");
	}

	@DisplayName("자신이 작성한 게시글 목록만 조회할 수 있다.")
	@Test
	void getQuestionPostsByMember() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		// when
		Slice<QuestionPostsByMemberResponse> postsByMember = memberRepository.getQuestionPostsByMember(member1,
			pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(2),
			() -> assertThat(postsByMember).extracting(QuestionPostsByMemberResponse::title)
				.containsExactly(
					"두 번째 게시글입니다.",
					"첫 번째 게시글입니다."
				),
			() -> assertThat(postsByMember).extracting(QuestionPostsByMemberResponse::questionPostId)
				.containsExactly(
					2L,
					1L
				)
		);
	}

	@DisplayName("자신이 댓글 단 게시글 목록만 조회할 수 있다.")
	@Test
	void getAnsweredQuestionPostsByMember() {
		// given
		Member member1 = MemberFixture.member();
		Member member2 = MemberFixture.member2();
		memberRepository.saveAll(List.of(member1, member2));

		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member1, "첫 번째 게시글입니다.");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member1, "두 번째 게시글입니다.");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member2, "세 번째 게시글입니다.");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		Answer answer1 = AnswerFixture.answer(questionPost1.getId(), member1);
		Answer answer2 = AnswerFixture.answer(questionPost1.getId(), member2);
		Answer answer3 = AnswerFixture.answer(questionPost2.getId(), member2);
		Answer answer4 = AnswerFixture.answer(questionPost3.getId(), member1);
		answerRepository.saveAll(List.of(answer1, answer2, answer3, answer4));

		// when
		Slice<AnsweredQuestionPostsByMemberResponse> postsByMember =
			memberRepository.getAnsweredQuestionPostsByMember(member1, pageRequest);

		// then
		Assertions.assertAll(
			() -> assertThat(postsByMember).hasSize(2),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsByMemberResponse::title)
				.containsExactly(
					"세 번째 게시글입니다.",
					"첫 번째 게시글입니다."
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsByMemberResponse::questionPostId)
				.containsExactly(
					3L,
					1L
				),
			() -> assertThat(postsByMember).extracting(AnsweredQuestionPostsByMemberResponse::answerId)
				.containsExactly(
					4L,
					1L
				)
		);
	}

	private Member createMember(String nickname, String socialName, String socialEmail, String officialEmail) {
		return Member.builder()
			.nickname(nickname)
			.socialName(socialName)
			.socialEmail(socialEmail)
			.officialEmail(officialEmail)
			.jobCategory(GAS)
			.jobGroup(ENGINEERING)
			.credit(10000)
			.build();

	}

}