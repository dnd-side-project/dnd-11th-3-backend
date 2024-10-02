package com.dnd.gongmuin.chat.dto;

import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.dto.response.AcceptChatResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomInfo;
import com.dnd.gongmuin.chat.dto.response.ChatRoomSimpleResponse;
import com.dnd.gongmuin.chat.dto.response.LatestChatMessage;
import com.dnd.gongmuin.chat.dto.response.RejectChatResponse;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.response.MemberInfo;

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
		Member chatPartner
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
			chatRoom.getStatus().getLabel()
		);
	}

	public static AcceptChatResponse toAcceptChatResponse(ChatRoom chatRoom) {
		return new AcceptChatResponse(
			chatRoom.getStatus().getLabel(),
			chatRoom.getAnswerer().getCredit()
		);
	}

	public static RejectChatResponse toRejectChatResponse(ChatRoom chatRoom) {
		return new RejectChatResponse(
			chatRoom.getStatus().getLabel()
		);
	}

	public static ChatRoomSimpleResponse toChatRoomSimpleResponse(
		ChatRoomInfo chatRoomInfo,
		LatestChatMessage latestChatMessage
	) {
		return new ChatRoomSimpleResponse(
			chatRoomInfo.chatRoomId(),
			new MemberInfo(
				chatRoomInfo.partnerId(),
				chatRoomInfo.partnerNickname(),
				chatRoomInfo.partnerJobGroup(),
				chatRoomInfo.partnerProfileImageNo()
			),
			latestChatMessage.content(),
			latestChatMessage.type(),
			latestChatMessage.createdAt().toString()
		);
	}

}
