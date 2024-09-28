package com.dnd.gongmuin.chat.dto.response;

import com.dnd.gongmuin.question_post.dto.response.MemberInfo;

public record ChatRoomSimpleResponse(
	Long chatRoomId,
	MemberInfo chatPartner,
	String latestMessage,
	String messageType,
	String messageCreatedAt
) {
}
