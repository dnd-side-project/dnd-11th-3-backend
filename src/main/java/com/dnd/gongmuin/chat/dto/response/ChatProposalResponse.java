package com.dnd.gongmuin.chat.dto.response;

import com.dnd.gongmuin.question_post.dto.response.MemberInfo;

public record ChatProposalResponse (
	Long chatRoomId,
	String chatStatus,
	boolean isInquirer,
	MemberInfo chatPartner,
	String latestMessage,
	String messageType,
	String messageCreatedAt
){}
