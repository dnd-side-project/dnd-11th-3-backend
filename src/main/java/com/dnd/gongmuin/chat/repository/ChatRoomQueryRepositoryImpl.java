package com.dnd.gongmuin.chat.repository;

import static com.dnd.gongmuin.chat.domain.QChatRoom.*;

import java.util.List;

import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chat.dto.response.QChatRoomInfo;
import com.dnd.gongmuin.member.domain.Member;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {
	private final JPAQueryFactory queryFactory;

	public List<ChatRoomInfo> getChatRoomsByMember(Member member) {
		return queryFactory
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
	}
}
