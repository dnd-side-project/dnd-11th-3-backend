package com.dnd.gongmuin.chatroom.repository;

import static com.dnd.gongmuin.chatroom.domain.QChatRoom.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.dnd.gongmuin.chatroom.dto.response.ChatRoomInfo;
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
				.or(chatRoom.answerer.id.eq(member.getId())))
			.fetch();

		boolean hasNext = hasNext(pageable.getPageSize(), content);
		return new SliceImpl<>(content, pageable, hasNext);
	}

	private <T> boolean hasNext(int pageSize, List<T> items) {
		if (items.size() <= pageSize) {
			return false;
		}
		items.remove(pageSize);
		return true;
	}
}
