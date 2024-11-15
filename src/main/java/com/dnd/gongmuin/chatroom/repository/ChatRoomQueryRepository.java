package com.dnd.gongmuin.chatroom.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.chatroom.dto.response.ChatProposalInfo;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.member.domain.Member;

public interface ChatRoomQueryRepository {

	Slice<ChatRoomInfo> getChatRoomsByMember(Member member, Pageable pageable);
	Slice<ChatProposalInfo> getChatProposalsByMember(Member member, Pageable pageable);

	List<Long> getAutoRejectedInquirerIds();

	void updateChatRoomStatusRejected();
}
