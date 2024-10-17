package com.dnd.gongmuin.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.chat.domain.ChatStatus;
import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.member.domain.Member;

public interface ChatRoomQueryRepository {
	Slice<ChatRoomInfo> getChatRoomsByMember(Member member, ChatStatus chatStatus, Pageable pageable);


	List<Long> getAutoRejectedInquirerIds();
}
