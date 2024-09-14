package com.dnd.gongmuin.chat.dto.response;

import com.dnd.gongmuin.question_post.dto.response.MemberInfo;

public record ChatRoomDetailResponse(
	Long questionPostId,
	String targetJobGroup,
	String title,
	MemberInfo receiverInfo,
	boolean isAccepted
) {
}
