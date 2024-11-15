package com.dnd.gongmuin.chat_inquiry.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat_inquiry.service.ChatInquiryService;
import com.dnd.gongmuin.chatroom.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatInquiryScheduler {

	private final ChatInquiryService chatInquiryService;

	@Transactional
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void rejectChatInquiry() {
		chatInquiryService.rejectChatAuto();
	}
}
