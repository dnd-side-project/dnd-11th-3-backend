package com.dnd.gongmuin.chat.dto.response;

import com.dnd.gongmuin.question_post.dto.response.MemberInfo;

public record CreateChatRoomResponse(
	Long questionPostId,
	String targetJobGroup,
	String title,
	MemberInfo receiverInfo,
	String chatStatus,
	int credit
) {
}
