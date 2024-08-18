package com.dnd.gongmuin.question_post.repository;

import static com.dnd.gongmuin.question_post.domain.QQuestionPost.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.QInteractionCount;
import com.dnd.gongmuin.question_post.domain.QQuestionPost;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.dto.response.QQuestionPostSimpleResponse;
import com.dnd.gongmuin.question_post.dto.response.QRecQuestionPostResponse;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;
import com.dnd.gongmuin.question_post.dto.response.RecQuestionPostResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionPostQueryRepositoryImpl implements QuestionPostQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<QuestionPostSimpleResponse> searchQuestionPosts(
		QuestionPostSearchCondition condition,
		Pageable pageable
	) {
		QQuestionPost questionPost = QQuestionPost.questionPost;
		QInteractionCount saved = new QInteractionCount("saved");
		QInteractionCount recommend = new QInteractionCount("recommend");

		List<QuestionPostSimpleResponse> content = queryFactory
			.select(new QQuestionPostSimpleResponse(
				questionPost,
				saved.count.coalesce(0),
				recommend.count.coalesce(0)
			))
			.from(questionPost)
			.leftJoin(saved)
			.on(questionPost.id.eq(saved.questionPostId)
				.and(saved.type.eq(InteractionType.SAVED)))
			.leftJoin(recommend)
			.on(questionPost.id.eq(recommend.questionPostId)
				.and(recommend.type.eq(InteractionType.RECOMMEND)))
			.where(
				keywordContains(condition.keyword()),
				jobGroupContains(condition.jobGroups()),
				isChosenEq(condition.isChosen())
			)
			.orderBy(questionPost.createdAt.desc())
			.limit(pageable.getPageSize() + 1L)
			.offset(pageable.getOffset())
			.fetch();
		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<RecQuestionPostResponse> getRecommendQuestionPosts(
		JobGroup targetJobGroup,
		Pageable pageable
	) {
		QQuestionPost questionPost = QQuestionPost.questionPost;
		QInteractionCount saved = new QInteractionCount("saved");
		QInteractionCount recommend = new QInteractionCount("recommend");

		List<RecQuestionPostResponse> content = queryFactory
			.select(new QRecQuestionPostResponse(
				questionPost,
				saved.count.coalesce(0),
				recommend.count.coalesce(0)
			))
			.from(questionPost)
			.leftJoin(saved)
			.on(questionPost.id.eq(saved.questionPostId)
				.and(saved.type.eq(InteractionType.SAVED)))
			.leftJoin(recommend)
			.on(questionPost.id.eq(recommend.questionPostId)
				.and(recommend.type.eq(InteractionType.RECOMMEND)))
			.where(
				questionPost.jobGroup.eq(targetJobGroup)
			)
			.orderBy(
				recommend.count.coalesce(0).desc(),
				saved.count.coalesce(0).desc(),
				questionPost.createdAt.desc()
			)
			.limit(pageable.getPageSize() + 1L)
			.offset(pageable.getOffset())
			.fetch();
		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	private BooleanExpression isChosenEq(Boolean isChosen) {
		if (isChosen == null) {
			return null;
		}
		if (Boolean.TRUE.equals(isChosen)) {
			return questionPost.isChosen.eq(Boolean.TRUE);
		} else {
			return questionPost.isChosen.eq(Boolean.FALSE);
		}
	}

	private BooleanExpression jobGroupContains(List<String> jobGroups) {
		if (jobGroups == null || jobGroups.isEmpty())
			return null; // 직군 필터링 선택 안할 때
		List<JobGroup> selectedJobGroups = JobGroup.from(jobGroups); // string -> enum
		return questionPost.jobGroup.in(selectedJobGroups);
	}

	private BooleanExpression keywordContains(String keyword) {
		return keyword != null ? questionPost.title.contains(keyword) : null;
	}

	private <T> boolean hasNext(int pageSize, List<T> items) {
		if (items.size() <= pageSize) {
			return false;
		}
		items.remove(pageSize);
		return true;
	}
}
