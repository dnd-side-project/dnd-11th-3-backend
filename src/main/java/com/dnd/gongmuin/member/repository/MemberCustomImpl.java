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
import com.dnd.gongmuin.post_interaction.domain.QInteractionCount;
import com.dnd.gongmuin.question_post.domain.QQuestionPost;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberCustomImpl implements MemberCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<QuestionPostsByMemberResponse> getQuestionPostsByMember(Member member, Pageable pageable) {
		QQuestionPost qp = QQuestionPost.questionPost;
		QInteractionCount ic = QInteractionCount.interactionCount;

		List<QuestionPostsByMemberResponse> content = queryFactory
			.select(new QQuestionPostsByMemberResponse(qp, ic))
			.from(qp)
			.leftJoin(ic)
			.on(qp.id.eq(ic.questionPostId))
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
		QInteractionCount ic = QInteractionCount.interactionCount;
		QAnswer aw = QAnswer.answer;

		List<AnsweredQuestionPostsByMemberResponse> content = queryFactory
			.select(new QAnsweredQuestionPostsByMemberResponse(qp, ic, aw))
			.from(qp)
			.join(aw)
			.on(qp.id.eq(aw.questionPostId))
			.leftJoin(ic)
			.on(qp.id.eq(ic.questionPostId))
			.where(aw.member.eq(member))
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
