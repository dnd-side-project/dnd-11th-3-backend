package com.dnd.gongmuin.question_post.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StopWatch;

import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.DataJpaTestSupport;
import com.dnd.gongmuin.credit_history.service.CreditHistoryService;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionJdbcRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.repository.QuestionPostJdbcRepository;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

@Import({QuestionPostService.class, CreditHistoryService.class})
class SearchQuestionPostTest extends DataJpaTestSupport {
	private final PageRequest pageRequest = PageRequest.of(0, 10);
	private Member member, questioner;

	@Autowired
	private QuestionPostService questionPostService;
	@Autowired
	private CreditHistoryService creditHistoryService;
	@Autowired
	private InteractionJdbcRepository interactionRepository;
	@Autowired
	private QuestionPostJdbcRepository questionPostJdbcRepository;
	@Autowired
	private QuestionPostRepository questionPostRepository;
	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void setup() {
		member = memberRepository.save(MemberFixture.member());
		questioner = memberRepository.save(MemberFixture.member2());

		List<QuestionPost> questionPosts = insertPosts();
		questionPostJdbcRepository.saveQuestionPosts(questionPosts);

		List<Interaction> interactions = insertInteractions();
		interactionRepository.saveInteractions(interactions);
	}

	@DisplayName("조회 결과의 memberId로 상호작용 여부를 조회한다.")
	@Test
	void searchQuestionPost() {
		QuestionPostSearchCondition condition = new QuestionPostSearchCondition(
			null,
			List.of("공업"),
			null
		);
		StopWatch stopWatch = new StopWatch("10,000개 데이터 검색 - stream");
		stopWatch.start();
		questionPostService.searchQuestionPost(member, condition, pageRequest);

		stopWatch.stop();
		System.out.println(stopWatch.prettyPrint());
	}

	private List<QuestionPost> insertPosts() {
		List<QuestionPost> questionPosts = new ArrayList<>();
		int threadCount = 10_000;
		for (int i = 0; i < threadCount; i++) {
			QuestionPost questionPost = QuestionPostFixture.questionPost(i + 1L, questioner);
			questionPosts.add(questionPost);
		}
		return questionPosts;
	}

	private List<Interaction> insertInteractions() {
		List<Interaction> interactions = new ArrayList<>();
		int threadCount = 10_000;
		for (int i = 0; i < threadCount; i++) {
			InteractionType type = i % 2 == 0 ? InteractionType.SAVED : InteractionType.RECOMMEND;
			Interaction interaction = InteractionFixture.interaction(i + 1L, type, member.getId(), i + 1L);
			interactions.add(interaction);
		}
		return interactions;
	}
}
