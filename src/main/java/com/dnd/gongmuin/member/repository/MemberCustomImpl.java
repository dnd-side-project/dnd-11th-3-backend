package com.dnd.gongmuin.member.repository;

import static com.dnd.gongmuin.credit_history.domain.QCreditHistory.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.answer.domain.QAnswer;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsResponse;
import com.dnd.gongmuin.member.dto.response.BookmarksResponse;
import com.dnd.gongmuin.member.dto.response.CreditHistoryResponse;
import com.dnd.gongmuin.member.dto.response.QAnsweredQuestionPostsResponse;
import com.dnd.gongmuin.member.dto.response.QBookmarksResponse;
import com.dnd.gongmuin.member.dto.response.QCreditHistoryResponse;
import com.dnd.gongmuin.member.dto.response.QQuestionPostsResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsResponse;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.QInteraction;
import com.dnd.gongmuin.post_interaction.domain.QInteractionCount;
import com.dnd.gongmuin.question_post.domain.QQuestionPost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberCustomImpl implements MemberCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<QuestionPostsResponse> getQuestionPostsByMember(Member member, Pageable pageable) {
		QQuestionPost qp = QQuestionPost.questionPost;
		QInteractionCount saved = new QInteractionCount("SAVED");
		QInteractionCount recommend = new QInteractionCount("RECOMMEND");

		List<QuestionPostsResponse> content = queryFactory
			.select(new QQuestionPostsResponse(
				qp,
				saved.count.coalesce(0).as("bookmarkCount"),
				recommend.count.coalesce(0).as("recommendCount")
			))
			.from(qp)
			.leftJoin(saved)
			.on(qp.id.eq(saved.questionPostId).and(saved.type.eq(InteractionType.SAVED)))
			.leftJoin(recommend)
			.on(qp.id.eq(recommend.questionPostId).and(recommend.type.eq(InteractionType.RECOMMEND)))
			.where(qp.member.eq(member))
			.orderBy(qp.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1L)
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<AnsweredQuestionPostsResponse> getAnsweredQuestionPostsByMember(
		Member member, Pageable pageable) {
		QQuestionPost qp = QQuestionPost.questionPost;
		QInteractionCount saved = new QInteractionCount("SAVED");
		QInteractionCount recommend = new QInteractionCount("RECOMMEND");
		QAnswer aw1 = new QAnswer("answer1");
		QAnswer aw2 = new QAnswer("answer2");

		List<AnsweredQuestionPostsResponse> content =
			queryFactory
				.select(new QAnsweredQuestionPostsResponse(
					qp,
					saved.count.coalesce(0).as("bookmarkCount"),
					recommend.count.coalesce(0).as("recommendCount"),
					aw1
				))
				.from(qp)
				.join(aw1)
				.on(aw1.id.eq(
					JPAExpressions
						.select(aw2.id)
						.from(aw2)
						.where(aw2.questionPostId.eq(qp.id)
							.and(aw2.member.eq(member))
							.and(aw2.createdAt.eq(
								JPAExpressions
									.select(aw2.createdAt.max())
									.from(aw2)
									.where(aw2.questionPostId.eq(qp.id)
										.and(aw2.member.eq(member)))
							)))
				))
				.leftJoin(saved)
				.on(qp.id.eq(saved.questionPostId).and(saved.type.eq(InteractionType.SAVED)))
				.leftJoin(recommend)
				.on(qp.id.eq(recommend.questionPostId).and(recommend.type.eq(InteractionType.RECOMMEND)))
				.orderBy(qp.createdAt.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize() + 1L)
				.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<BookmarksResponse> getBookmarksByMember(Member member, Pageable pageable) {
		QQuestionPost qp = QQuestionPost.questionPost;
		QInteraction ir = QInteraction.interaction;
		QInteractionCount saved = new QInteractionCount("SAVED");
		QInteractionCount recommend = new QInteractionCount("RECOMMEND");

		List<BookmarksResponse> content = queryFactory
			.select(new QBookmarksResponse(
				qp,
				saved.count.coalesce(0).as("bookmarkCount"),
				recommend.count.coalesce(0).as("recommendCount")
			))
			.from(qp)
			.join(ir)
			.on(qp.id.eq(ir.questionPostId).and(ir.type.eq(InteractionType.SAVED)))
			.leftJoin(saved)
			.on(qp.id.eq(saved.questionPostId).and(saved.type.eq(InteractionType.SAVED)))
			.leftJoin(recommend)
			.on(qp.id.eq(recommend.questionPostId).and(recommend.type.eq(InteractionType.RECOMMEND)))
			.where(ir.memberId.eq(member.getId()))
			.orderBy(qp.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1L)
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<CreditHistoryResponse> getCreditHistoryByMember(String type, Member member,
		Pageable pageable) {
		List<CreditHistoryResponse> content = queryFactory
			.select(new QCreditHistoryResponse(
				creditHistory
			))
			.from(creditHistory)
			.where(
				creditHistory.member.eq(member),
				creditTypeEq(type)
			)
			.orderBy(creditHistory.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1L)
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	private BooleanExpression creditTypeEq(String type) {
		if (type == null || type.isEmpty() || "전체".equals(type)) {
			return null;
		}

		return creditHistory.type.in(CreditType.fromDetail(type));
	}

	private <T> boolean hasNext(int pageSize, List<T> content) {
		if (content.size() <= pageSize) {
			return false;
		}
		content.remove(pageSize);
		return true;
	}
}
