package com.dnd.gongmuin.chat.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.chat.domain.ChatStatus;
import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.member.domain.Member;

public interface ChatRoomQueryRepository {

	Slice<ChatRoomInfo> getChatRoomsByMember(Member member, ChatStatus chatStatus, Pageable pageable);


	List<Long> getAutoRejectedInquirerIds();
	void updateChatRoomStatusRejected();
	void refundInMemberIds(List<Long> memberIds, int credit);
}
