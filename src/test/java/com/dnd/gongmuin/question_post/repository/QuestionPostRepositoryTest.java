package com.dnd.gongmuin.question_post.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("[질문글 동적 쿼리 테스트]")
class QuestionPostRepositoryTest extends DataJpaTestSupport {

	private final PageRequest pageRequest = PageRequest.of(0, 10);
	private Member member;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private InteractionRepository interactionRepository;

	@Autowired
	private InteractionCountRepository interactionCountRepository;

	@BeforeEach
	void setup() {
		member = memberRepository.save(MemberFixture.member());
	}

	@DisplayName("검색어로 필터링할 수 있다.")
	@Test
	void question_post_search_filter() {
		//given
		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member, "신규 발령 났어요");
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member, "신규 발령자가 알아야 할게 있을까요?");
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member, "승진하고 싶어요");
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		QuestionPostSearchCondition condition = new QuestionPostSearchCondition(
			"신규",
			null,
			null
		);

		//when
		List<QuestionPostSimpleResponse> responses = questionPostRepository
			.searchQuestionPosts(condition, pageRequest)
			.getContent();

		//then
		Assertions.assertAll(
			() -> assertThat(responses).hasSize(2),
			() -> assertThat(responses.get(0).questionPostId()).isEqualTo(questionPost1.getId()),
			() -> assertThat(responses.get(1).questionPostId()).isEqualTo(questionPost2.getId())
		);
	}

	@DisplayName("직군으로 질문글을 필터링할 수 있다.")
	@Test
	void question_post_jobgroup_filter() {
		//given
		QuestionPost questionPost1 = QuestionPostFixture.questionPost("기계", member);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost("행정", member);
		QuestionPost questionPost3 = QuestionPostFixture.questionPost("공업", member);

		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		QuestionPostSearchCondition condition = new QuestionPostSearchCondition(
			null,
			List.of("행정", "기계"),
			null
		);

		//when
		List<QuestionPostSimpleResponse> responses = questionPostRepository
			.searchQuestionPosts(condition, pageRequest)
			.getContent();

		//then
		Assertions.assertAll(
			() -> assertThat(responses).hasSize(2),
			() -> assertThat(responses.get(0).questionPostId()).isEqualTo(questionPost1.getId()),
			() -> assertThat(responses.get(1).questionPostId()).isEqualTo(questionPost2.getId())
		);
	}

	@DisplayName("채택 여부로 질문글을 필터링할 수 있다.")
	@Test
	void question_post_ischosen_filter() {
		//given
		QuestionPost questionPost1 = QuestionPostFixture.questionPost(member);
		QuestionPost questionPost2 = QuestionPostFixture.questionPost(member);
		QuestionPost questionPost3 = QuestionPostFixture.questionPost(member);
		ReflectionTestUtils.setField(questionPost1, "isChosen", true);
		questionPostRepository.saveAll(List.of(questionPost1, questionPost2, questionPost3));

		QuestionPostSearchCondition condition = new QuestionPostSearchCondition(
			null,
			null,
			true
		);

		//when
		List<QuestionPostSimpleResponse> responses = questionPostRepository
			.searchQuestionPosts(condition, pageRequest)
			.getContent();

		//then
		Assertions.assertAll(
			() -> assertThat(responses).hasSize(1),
			() -> assertThat(responses.get(0).questionPostId()).isEqualTo(questionPost1.getId())
		);
	}

	@DisplayName("추천수, 저장수를 함께 조회할 수 있다.")
	@Test
	void question_post_join_interactionCount() {
		//given
		QuestionPost questionPost = QuestionPostFixture.questionPost(member);
		questionPostRepository.save(questionPost);

		QuestionPostSearchCondition condition = new QuestionPostSearchCondition(
			null,
			null,
			null
		);

		interactPost(questionPost.getId(), InteractionType.SAVED);
		interactPost(questionPost.getId(), InteractionType.RECOMMEND);

		//when
		List<QuestionPostSimpleResponse> responses = questionPostRepository
			.searchQuestionPosts(condition, pageRequest)
			.getContent();
		System.out.println(responses);
		//then
		Assertions.assertAll(
			() -> assertThat(responses).hasSize(1),
			() -> assertThat(responses.get(0).questionPostId()).isEqualTo(questionPost.getId()),
			() -> assertThat(responses.get(0).savedCount()).isEqualTo(1),
			() -> assertThat(responses.get(0).recommendCount()).isEqualTo(1)
		);
	}

	private void interactPost(Long questionPostId, InteractionType type){
		Interaction interaction =
			InteractionFixture.interaction(type, 2L, questionPostId);
		interactionRepository.save(interaction);
		InteractionCount interactionCount =
			InteractionCountFixture.interactionCount(type, questionPostId);
		interactionCountRepository.save(interactionCount);
	}

}