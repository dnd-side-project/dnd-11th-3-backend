package com.dnd.gongmuin.chat.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.repository.ChatRoomRepository;
import com.dnd.gongmuin.credit_history.domain.CreditType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatScheduler {
	/*
	- 2000 크레딧 반환
		- 반환 이력 추가
	- 상태 거절로 바꾸기
	 */
	private final ChatRoomRepository chatRoomRepository;
	private static final int CHAT_REWARD = 2000;

	@Transactional
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	public void rejectChatRequest() {
		List<Long> rejectedInquirerIds = chatRoomRepository.getAutoRejectedInquirerIds();
		chatRoomRepository.updateChatRoomStatusRejected();
		chatRoomRepository.refundInMemberIds(rejectedInquirerIds, CHAT_REWARD);
		chatRoomRepository.saveCreditHistoryInMemberIds(
			rejectedInquirerIds, CreditType.CHAT_REFUND, CHAT_REWARD
		);
	}
}
