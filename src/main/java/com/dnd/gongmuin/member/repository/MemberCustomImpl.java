package com.dnd.gongmuin.member.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.answer.domain.QAnswer;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.AnsweredQuestionPostsByMemberResponse;
import com.dnd.gongmuin.member.dto.response.QAnsweredQuestionPostsByMemberResponse;
import com.dnd.gongmuin.member.dto.response.QQuestionPostsByMemberResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsByMemberResponse;
import com.dnd.gongmuin.post_interaction.domain.InteractionType;
import com.dnd.gongmuin.post_interaction.domain.QInteractionCount;
import com.dnd.gongmuin.question_post.domain.QQuestionPost;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberCustomImpl implements MemberCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<QuestionPostsByMemberResponse> getQuestionPostsByMember(Member member, Pageable pageable) {
		QQuestionPost qp = QQuestionPost.questionPost;
		QInteractionCount saved = new QInteractionCount("SAVED");
		QInteractionCount recommend = new QInteractionCount("RECOMMEND");

		List<QuestionPostsByMemberResponse> content = queryFactory
			.select(new QQuestionPostsByMemberResponse(qp, saved, recommend))
			.from(qp)
			.leftJoin(saved)
			.on(qp.id.eq(saved.questionPostId).and(saved.type.eq(InteractionType.SAVED)))
			.leftJoin(recommend)
			.on(qp.id.eq(recommend.questionPostId).and(recommend.type.eq(InteractionType.RECOMMEND)))
			.where(qp.member.eq(member))
			.orderBy(qp.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public Slice<AnsweredQuestionPostsByMemberResponse> getAnsweredQuestionPostsByMember(
		Member member, Pageable pageable) {
		QQuestionPost qp = QQuestionPost.questionPost;
		QInteractionCount saved = new QInteractionCount("SAVED");
		QInteractionCount recommend = new QInteractionCount("RECOMMEND");
		QAnswer aw1 = new QAnswer("answer1");
		QAnswer aw2 = new QAnswer("answer2");

		List<AnsweredQuestionPostsByMemberResponse> content =
			queryFactory
				.select(new QAnsweredQuestionPostsByMemberResponse(qp, saved, recommend, aw1))
				.from(qp)
				.join(aw1)
				.on(aw1.id.eq(
					JPAExpressions
						.select(aw2.id)
						.from(aw2)
						.where(aw2.questionPostId.eq(qp.id)
							.and(aw2.member.eq(member))
							.and(aw2.updatedAt.eq(
								JPAExpressions
									.select(aw2.updatedAt.max())
									.from(aw2)
									.where(aw2.questionPostId.eq(qp.id)
										.and(aw2.member.eq(member)))
							)))
				))
				.leftJoin(saved)
				.on(qp.id.eq(saved.questionPostId).and(saved.type.eq(InteractionType.SAVED)))
				.leftJoin(recommend)
				.on(qp.id.eq(recommend.questionPostId).and(recommend.type.eq(InteractionType.RECOMMEND)))
				.orderBy(qp.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize() + 1)
				.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	private <T> boolean hasNext(int pageSize, List<T> content) {
		if (content.size() <= pageSize) {
			return false;
		}
		content.remove(pageSize);
		return true;
	}
}
