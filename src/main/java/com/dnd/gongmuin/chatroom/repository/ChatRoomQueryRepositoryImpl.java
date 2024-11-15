package com.dnd.gongmuin.chatroom.repository;


import static com.dnd.gongmuin.chat.domain.QChatRoom.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.chatroom.domain.InquiryStatus;
import com.dnd.gongmuin.chatroom.dto.response.ChatProposalInfo;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chat.dto.response.QChatProposalInfo;
import com.dnd.gongmuin.chat.dto.response.QChatRoomInfo;
import com.dnd.gongmuin.chatroom.domain.QChatRoom;
import com.dnd.gongmuin.chatroom.dto.response.QChatRoomInfo;
import com.dnd.gongmuin.member.domain.Member;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Slice<ChatRoomInfo> getChatRoomsByMember(
		Member member,
		Pageable pageable
	) {
		List<ChatRoomInfo> content = queryFactory
			.select(new QChatRoomInfo(
				QChatRoom.chatRoom.id,
				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.id)
					.otherwise(QChatRoom.chatRoom.inquirer.id),

				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.nickname)
					.otherwise(QChatRoom.chatRoom.inquirer.nickname),
				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.jobGroup)
					.otherwise(QChatRoom.chatRoom.inquirer.jobGroup),
				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.profileImageNo)
					.otherwise(QChatRoom.chatRoom.inquirer.profileImageNo)
			))
			.from(QChatRoom.chatRoom)
			.where(QChatRoom.chatRoom.inquirer.id.eq(member.getId())
				.or(QChatRoom.chatRoom.answerer.id.eq(member.getId()))
				.and(QChatRoom.chatRoom.status.eq(InquiryStatus.ACCEPTED)))
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	public Slice<ChatProposalInfo> getChatProposalsByMember(Member member, Pageable pageable){
		List<ChatProposalInfo> content = queryFactory
			.select(new QChatProposalInfo(
				QChatRoom.chatRoom.id,
				QChatRoom.chatRoom.status,
				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(true)
					.otherwise(false),
				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.id)
					.otherwise(QChatRoom.chatRoom.inquirer.id),

				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.nickname)
					.otherwise(QChatRoom.chatRoom.inquirer.nickname),
				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.jobGroup)
					.otherwise(QChatRoom.chatRoom.inquirer.jobGroup),
				new CaseBuilder()
					.when(QChatRoom.chatRoom.inquirer.id.eq(member.getId()))
					.then(QChatRoom.chatRoom.answerer.profileImageNo)
					.otherwise(QChatRoom.chatRoom.inquirer.profileImageNo)
			))
			.from(QChatRoom.chatRoom)
			.where(QChatRoom.chatRoom.inquirer.id.eq(member.getId())
				.or(QChatRoom.chatRoom.answerer.id.eq(member.getId()))
				.and(QChatRoom.chatRoom.status.in(List.of(InquiryStatus.REJECTED, InquiryStatus.PENDING))))
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	public List<Long> getAutoRejectedInquirerIds() {
		return queryFactory
			.select(QChatRoom.chatRoom.inquirer.id)
			.from(QChatRoom.chatRoom)
			.where(
				QChatRoom.chatRoom.createdAt.loe(LocalDateTime.now().minusWeeks(1)),
				QChatRoom.chatRoom.status.eq(InquiryStatus.PENDING)
			)
			.fetch();
	}

	public void updateChatRoomStatusRejected() {
		queryFactory.update(QChatRoom.chatRoom)
			.set(QChatRoom.chatRoom.status, InquiryStatus.REJECTED)
			.where(
				QChatRoom.chatRoom.createdAt.loe(LocalDateTime.now().minusWeeks(1)),
				QChatRoom.chatRoom.status.eq(InquiryStatus.PENDING)
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
