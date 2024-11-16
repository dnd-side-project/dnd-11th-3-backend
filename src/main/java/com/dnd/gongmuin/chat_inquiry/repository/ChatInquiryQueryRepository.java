package com.dnd.gongmuin.chat_inquiry.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.chat_inquiry.dto.ChatInquiryResponse;
import com.dnd.gongmuin.member.domain.Member;

public interface ChatInquiryQueryRepository {
	Slice<ChatInquiryResponse> getChatInquiresByMember(Member member, Pageable pageable);

	List<Long> getAutoRejectedInquirerIds();

	void updateChatInquiryStatusRejected();
}
