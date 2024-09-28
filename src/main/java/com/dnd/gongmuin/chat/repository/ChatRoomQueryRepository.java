package com.dnd.gongmuin.chat.repository;

import java.util.List;

import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.member.domain.Member;

public interface ChatRoomQueryRepository {
	List<ChatRoomInfo> getChatRoomsByMember(Member member);
}
