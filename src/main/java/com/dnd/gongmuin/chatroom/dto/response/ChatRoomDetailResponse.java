package com.dnd.gongmuin.chatroom.dto.response;

import com.dnd.gongmuin.member.dto.response.MemberInfo;

public record ChatRoomDetailResponse(
	Long questionPostId,
	String targetJobGroup,
	String title,
	MemberInfo chatPartner,
	boolean isInquirer
) {
}
