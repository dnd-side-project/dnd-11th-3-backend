package com.dnd.gongmuin.chat.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.service.ChatRoomService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatScheduler {

	private final ChatRoomService chatRoomService;

	@Transactional
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void rejectChatRequest() {
		chatRoomService.rejectChatAuto();
	}

}
