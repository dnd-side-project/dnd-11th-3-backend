package com.dnd.gongmuin.chat.dto;

import com.dnd.gongmuin.chat.domain.ChatRoom;
import com.dnd.gongmuin.chat.dto.response.AcceptChatResponse;
import com.dnd.gongmuin.chat.dto.response.ChatRoomDetailResponse;
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

	public static ChatRoomDetailResponse toChatRoomDetailResponse(ChatRoom chatRoom) {
		QuestionPost questionPost = chatRoom.getQuestionPost();
		Member answerer = chatRoom.getAnswerer(); // 요청자만 채팅방 생성 가능 -> 상태방: 답변자

		return new ChatRoomDetailResponse(
			questionPost.getId(),
			questionPost.getJobGroup().getLabel(),
			questionPost.getTitle(),
			new MemberInfo(
				answerer.getId(),
				answerer.getNickname(),
				answerer.getJobGroup().getLabel(),
				answerer.getProfileImageNo()
			),
			chatRoom.getStatus().getLabel()
		);
	}

	public static AcceptChatResponse toAcceptChatResponse(ChatRoom chatRoom){
		return new AcceptChatResponse(
			chatRoom.getStatus().getLabel(),
			chatRoom.getAnswerer().getCredit()
		);
	}

}
