package com.dnd.gongmuin.post_interaction.service;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dnd.gongmuin.common.fixture.InteractionCountFixture;
import com.dnd.gongmuin.common.fixture.InteractionFixture;
import com.dnd.gongmuin.common.fixture.MemberFixture;
import com.dnd.gongmuin.common.fixture.QuestionPostFixture;
import com.dnd.gongmuin.common.support.TestContainerSupport;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.post_interaction.domain.Interaction;
import com.dnd.gongmuin.post_interaction.domain.InteractionCount;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.repository.InteractionCountRepository;
import com.dnd.gongmuin.post_interaction.repository.InteractionRepository;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.repository.QuestionPostRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class InteractionTransactionTest extends TestContainerSupport {

	private final Member questioner = MemberFixture.member(1L);

	@Autowired
	private InteractionRepository interactionRepository;

	@Autowired
	private InteractionCountRepository interactionCountRepository;

	@Autowired
	private QuestionPostRepository questionPostRepository;

	@Autowired
	private InteractionService interactionService;

	@DisplayName("동시성 게시글 좋아요 카운트")
	@Test
	void lockingPostInteractionCountActive() throws InterruptedException {
		// given
		QuestionPost savedQuestion = questionPostRepository.save(QuestionPostFixture.questionPost(questioner));
		Interaction savedInteraction = interactionRepository.save(InteractionFixture.interaction(
			InteractionType.RECOMMEND, questioner.getId(), savedQuestion.getId())
		);
		InteractionCount savedInteractionCnt = interactionCountRepository.save(
			InteractionCountFixture.interactionCount(InteractionType.RECOMMEND, savedQuestion.getId())
		);

		int threadCount = 10;
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicLong memberIdGenerator = new AtomicLong(234);

		// when
		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					long uniqueMemberId = memberIdGenerator.getAndIncrement();
					interactionService.activateInteraction(savedQuestion.getId(), uniqueMemberId,
						InteractionType.RECOMMEND);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();//다른 쓰레드에서 수행중인 작업이 완료될때까지 기다려줌

		// when
		InteractionCount result = interactionCountRepository.findById(savedInteractionCnt.getId()).get();

		// then
		assertThat(result.getCount()).isEqualTo(threadCount + 1);

	}
}

