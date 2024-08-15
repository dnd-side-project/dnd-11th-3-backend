package com.dnd.gongmuin.member.repository;

import static com.dnd.gongmuin.post_interaction.domain.QPostInteractionCount.*;
import static com.dnd.gongmuin.question_post.domain.QQuestionPost.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.QQuestionPostsByMemberResponse;
import com.dnd.gongmuin.member.dto.response.QuestionPostsByMemberResponse;
import com.dnd.gongmuin.post_interaction.domain.QPostInteractionCount;
import com.dnd.gongmuin.question_post.domain.QQuestionPost;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberCustomImpl implements MemberCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<QuestionPostsByMemberResponse> getQuestionPostsByMember(Member member, Pageable pageable) {
		QQuestionPost qp = questionPost;
		QPostInteractionCount ic = postInteractionCount;

		List<QuestionPostsByMemberResponse> content = queryFactory
			.select(new QQuestionPostsByMemberResponse(qp, ic))
			.from(qp)
			.leftJoin(ic)
			.on(qp.id.eq(ic.questionPostId))
			.where(qp.member.eq(member))
			.orderBy(questionPost.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);

		return new SliceImpl<>(content, pageable, hasNext);
	}

	private boolean hasNext(int pageSize, List<QuestionPostsByMemberResponse> content) {
		if (content.size() <= pageSize) {
			return false;
		}
		content.remove(pageSize);
		return true;
	}
}
