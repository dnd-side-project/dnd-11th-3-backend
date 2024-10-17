package com.dnd.gongmuin.chat.repository;

import static com.dnd.gongmuin.chat.domain.QChatRoom.*;
import static com.dnd.gongmuin.member.domain.QMember.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.domain.ChatStatus;
import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chat.dto.response.QChatRoomInfo;
import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

	private final JPAQueryFactory queryFactory;
	private final MemberRepository memberRepository;
	private final CreditHistoryRepository creditHistoryRepository;

	public Slice<ChatRoomInfo> getChatRoomsByMember(
		Member member,
		ChatStatus chatStatus,
		Pageable pageable
	) {
		List<ChatRoomInfo> content = queryFactory
			.select(new QChatRoomInfo(
				chatRoom.id,
				new CaseBuilder()
					.when(chatRoom.inquirer.id.eq(member.getId()))
					.then(chatRoom.answerer.id)
					.otherwise(chatRoom.inquirer.id),
				new CaseBuilder()
					.when(chatRoom.inquirer.id.eq(member.getId()))
					.then(chatRoom.answerer.nickname)
					.otherwise(chatRoom.inquirer.nickname),
				new CaseBuilder()
					.when(chatRoom.inquirer.id.eq(member.getId()))
					.then(chatRoom.answerer.jobGroup)
					.otherwise(chatRoom.inquirer.jobGroup),
				new CaseBuilder()
					.when(chatRoom.inquirer.id.eq(member.getId()))
					.then(chatRoom.answerer.profileImageNo)
					.otherwise(chatRoom.inquirer.profileImageNo)
			))
			.from(chatRoom)
			.where(chatRoom.inquirer.id.eq(member.getId())
				.or(chatRoom.answerer.id.eq(member.getId()))
				.and(chatRoom.status.eq(chatStatus)))
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	public List<Long> getAutoRejectedInquirerIds() {
		return queryFactory
			.select(chatRoom.inquirer.id)
			.from(chatRoom)
			.where(
				chatRoom.createdAt.loe(LocalDateTime.now().minusWeeks(1)),
				chatRoom.status.eq(ChatStatus.PENDING)
			)
			.fetch();
	}

	@Transactional
	public void updateChatRoomStatusRejected() {
		queryFactory.update(chatRoom)
			.set(chatRoom.status, ChatStatus.REJECTED)
			.where(
				chatRoom.createdAt.loe(LocalDateTime.now().minusWeeks(1)),
				chatRoom.status.eq(ChatStatus.PENDING)
			)
			.execute();
	}

	@Transactional
	public void refundInMemberIds(List<Long> memberIds, int credit) {
		queryFactory
			.update(member)
			.set(member.credit, member.credit.add(credit))
			.where(member.id.in(memberIds))
			.execute();
	}

	public void saveCreditHistoryInMemberIds(List<Long> memberIds, CreditType type, int credit) {
		List<Member> inquirers = memberRepository.findAllById(memberIds);
		List<CreditHistory> histories = inquirers.stream()
			.map(inquirer -> CreditHistory.of(type, credit, inquirer))
			.toList();
		creditHistoryRepository.saveAll(histories);
	}



	private <T> boolean hasNext(int pageSize, List<T> items) {
		if (items.size() <= pageSize) {
			return false;
		}
		items.remove(pageSize);
		return true;
	}
}
