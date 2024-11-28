package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.member.domain.Member;
import com.querydsl.core.annotations.QueryProjection;

public record RejectChatInquiryDto(
	Long chatInquiryId,
	Member inquirer,
	Member answer
) {
	@QueryProjection
	public RejectChatInquiryDto(
		ChatInquiry chatInquiry
	) {
		this(
			chatInquiry.getId(),
			chatInquiry.getInquirer(),
			chatInquiry.getAnswerer()
		);
	}
}
