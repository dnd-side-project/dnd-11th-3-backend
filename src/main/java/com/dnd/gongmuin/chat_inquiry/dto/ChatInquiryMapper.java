package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.chatroom.dto.response.AcceptChatResponse;
import com.dnd.gongmuin.chatroom.dto.response.RejectChatResponse;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatInquiryMapper {

	public static ChatInquiry toChatInquiry(
		QuestionPost questionPost,
		Member inquirer,
		Member answerer,
		String message
	) {
		return ChatInquiry.of(
			questionPost,
			inquirer,
			answerer,
			message
		);
	}

	public static CreateChatInquiryResponse toCreateChatInquiryResponse(
		ChatInquiry chatInquiry
	) {
		Member answerer = chatInquiry.getAnswerer();
		return new CreateChatInquiryResponse(
			chatInquiry.getId(),
			chatInquiry.getMessage(),
			InquiryStatus.PENDING.getLabel(),
			answerer.getId(),
			answerer.getNickname(),
			answerer.getJobGroup().getLabel(),
			answerer.getProfileImageNo()
		);
	}
}
