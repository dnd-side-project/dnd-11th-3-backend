package com.dnd.gongmuin.chat_inquiry.repository;

import static com.dnd.gongmuin.chat_inquiry.domain.QChatInquiry.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.QChatInquiryResponse;
import com.dnd.gongmuin.chat_inquiry.dto.QRejectChatInquiryDto;
import com.dnd.gongmuin.chat_inquiry.dto.RejectChatInquiryDto;
import com.dnd.gongmuin.member.domain.Member;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatInquiryQueryRepositoryImpl implements ChatInquiryQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<ChatInquiryResponse> getChatInquiresByMember(Member member, Pageable pageable) {
		List<ChatInquiryResponse> content = queryFactory
			.select(new QChatInquiryResponse(
				chatInquiry,
				new CaseBuilder()
					.when(chatInquiry.inquirer.id.eq(member.getId()))
					.then(true)
					.otherwise(false)
			))
			.from(chatInquiry)
			.where(chatInquiry.inquirer.id.eq(member.getId())
				.or(chatInquiry.answerer.id.eq(member.getId()))
				.and(chatInquiry.status.in(List.of(InquiryStatus.REJECTED, InquiryStatus.PENDING))))
			.orderBy(chatInquiry.createdAt.desc())
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	public List<Long> getAutoRejectedInquirerIds() {
		return queryFactory
			.select(chatInquiry.inquirer.id)
			.from(chatInquiry)
			.where(
				chatInquiry.createdAt.loe(LocalDateTime.now().minusWeeks(1)),
				chatInquiry.status.eq(InquiryStatus.PENDING)
			)
			.fetch();
	}

	public List<RejectChatInquiryDto> getAutoRejectedChatInquiry() {
		return queryFactory
			.select(new QRejectChatInquiryDto(
				chatInquiry
			))
			.from(chatInquiry)
			.where(
				chatInquiry.createdAt.loe(LocalDateTime.now().minusWeeks(1)),
				chatInquiry.status.eq(InquiryStatus.PENDING)
			)
			.fetch();
	}

	public void updateChatInquiryStatusRejected() {
		queryFactory.update(chatInquiry)
			.set(chatInquiry.status, InquiryStatus.REJECTED)
			.where(
				chatInquiry.createdAt.loe(LocalDateTime.now().minusWeeks(1)),
				chatInquiry.status.eq(InquiryStatus.PENDING)
			)
			.execute();
	}

	private <T> boolean hasNext(int pageSize, List<T> items) {
		if (items.size() <= pageSize) {
			return false;
		}
		items.remove(pageSize);
		return true;
	}
}
