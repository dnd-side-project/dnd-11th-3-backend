package com.dnd.gongmuin.chatroom.dto;

import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chatroom.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chatroom.dto.response.LatestChatMessage;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.dto.response.MemberInfo;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomMapper {

	public static ChatRoom toChatRoom(
		QuestionPost questionPost,
		Member inquirer,
		Member answerer
	) {
		return ChatRoom.of(
			questionPost,
			inquirer,
			answerer
		);
	}

	public static ChatRoomDetailResponse toChatRoomDetailResponse(
		ChatRoom chatRoom,
		Member chatPartner,
		boolean isInquirer
	) {
		QuestionPost questionPost = chatRoom.getQuestionPost();
		return new ChatRoomDetailResponse(
			questionPost.getId(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getTitle(),
			new MemberInfo(
				chatPartner.getId(),
				chatPartner.getNickname(),
				chatPartner.getJobGroup().getLabel(),
				chatPartner.getProfileImageNo()
			),
			isInquirer
		);
	}

	public static ChatRoomSimpleResponse toChatRoomSimpleResponse(
		ChatRoomInfo chatRoomInfo,
		LatestChatMessage latestChatMessage
	) {
		String content = null;
		String type = null;
		String createdAt = null;

		if (latestChatMessage != null) {
			content = latestChatMessage.content();
			type = latestChatMessage.type();
			createdAt = latestChatMessage.createdAt().toString();
		}

		return new ChatRoomSimpleResponse(
			chatRoomInfo.chatRoomId(),
			new MemberInfo(
				chatRoomInfo.partnerId(),
				chatRoomInfo.partnerNickname(),
				chatRoomInfo.partnerJobGroup(),
				chatRoomInfo.partnerProfileImageNo()
			),
			content,
			type,
			createdAt
		);
	}
}
