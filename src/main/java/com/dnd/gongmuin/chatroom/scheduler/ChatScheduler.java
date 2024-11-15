package com.dnd.gongmuin.chatroom.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chatroom.service.ChatRoomService;

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
