package com.dnd.gongmuin.chat_inquiry.dto;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.chat_inquiry.domain.InquiryStatus;
import com.dnd.gongmuin.chatroom.domain.ChatRoom;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.member.dto.response.MemberInfo;

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
			new MemberInfo(
				answerer.getId(),
				answerer.getNickname(),
				answerer.getJobGroup().getLabel(),
				answerer.getProfileImageNo()
			)
		);
	}

	public static ChatInquiryDetailResponse toChatInquiryDetailResponse(
		ChatInquiry chatInquiry,
		Member member
	) {
		boolean isInquirer = chatInquiry.getInquirer().equals(member);
		Member chatPartner = isInquirer ? chatInquiry.getAnswerer() : chatInquiry.getInquirer();
		return new ChatInquiryDetailResponse(chatInquiry.getId(),
			chatInquiry.getMessage(),
			chatInquiry.getStatus().getLabel(),
			isInquirer,
			new MemberInfo(
				chatPartner.getId(),
				chatPartner.getNickname(),
				chatPartner.getJobGroup().getLabel(),
				chatPartner.getProfileImageNo()
			)
		);
	}

	public static AcceptChatResponse toAcceptChatResponse(
		ChatInquiry chatInquiry,
		ChatRoom chatRoom
	) {
		return new AcceptChatResponse(
			chatRoom.getId(),
			chatInquiry.getStatus().getLabel(),
			chatInquiry.getAnswerer().getCredit()
		);
	}

	public static RejectChatResponse toRejectChatResponse(ChatInquiry chatInquiry) {
		return new RejectChatResponse(
			chatInquiry.getStatus().getLabel()
		);
	}
}
