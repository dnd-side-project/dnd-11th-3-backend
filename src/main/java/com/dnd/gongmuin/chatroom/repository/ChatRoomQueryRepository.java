package com.dnd.gongmuin.chatroom.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.chatroom.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.member.domain.Member;

public interface ChatRoomQueryRepository {

	Slice<ChatRoomInfo> getChatRoomsByMember(Member member, Pageable pageable);
}
