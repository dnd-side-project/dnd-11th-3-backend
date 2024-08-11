package com.dnd.gongmuin.question_post.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;

@DisplayName("[질문글 동적 쿼리 테스트]")
class QuestionPostQueryRepositoryImplTest extends DataJpaTestSupport {

	private final PageRequest pageRequest = PageRequest.of(0, 10);

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private QuestionPostQueryRepository questionPostQueryRepository;

	@DisplayName("[카테고리로 질문글을 필터링할 수 있다.]")
	@Test
	void question_post_category_filter() {
		//given
		Member member = memberRepository.save(MemberFixture.member());
		QuestionPost questionPost1 = QuestionPostFixture.questionPost("기계", member);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost("행정", member);
		QuestionPost questionPost3 = QuestionPostFixture.questionPost("공업", member);

		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		QuestionPostSearchCondition condition = new QuestionPostSearchCondition(
			"",
			List.of("행정", "기계"),
			null
		);

		//when
		List<QuestionPost> questionPosts = questionPostQueryRepository
			.searchQuestionPosts(condition, pageRequest)
			.getContent();

		//then
		Assertions.assertAll(
			() -> assertThat(questionPosts).hasSize(2),
			() -> assertThat(questionPosts).containsExactly(questionPost1, questionPost2)
		);
	}

}