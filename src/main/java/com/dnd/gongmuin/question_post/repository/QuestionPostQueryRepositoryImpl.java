package com.dnd.gongmuin.question_post.repository;

import static com.dnd.gongmuin.question_post.domain.QQuestionPost.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.member.domain.JobGroup;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionPostQueryRepositoryImpl implements QuestionPostQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<QuestionPost> searchQuestionPosts(QuestionPostSearchCondition condition, Pageable pageable) {
		List<QuestionPost> content = queryFactory.select(questionPost)
			.from(questionPost)
			.where(
				keywordContains(condition.keyword()),
				jobGroupContains(condition.jobGroups())
			)
			.limit(pageable.getPageSize() + 1L)
			.offset(pageable.getOffset())
			.fetch();
		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	private BooleanExpression jobGroupContains(List<String> jobGroups) {
		if (jobGroups==null || jobGroups.isEmpty()) return null; // 직군 필터링 선택 안할 때
		List<JobGroup> selectedJobGroups = JobGroup.of(jobGroups); // string -> enum
		return questionPost.jobGroup.in(selectedJobGroups);
	}

	private BooleanExpression keywordContains(String keyword) {
		return keyword != null ? questionPost.title.contains(keyword) : null;
	}

	private boolean hasNext(int pageSize, List<QuestionPost> questionPosts) {
		if (questionPosts.size() <= pageSize) {
			return false;
		}
		questionPosts.remove(pageSize);
		return true;
	}
}
