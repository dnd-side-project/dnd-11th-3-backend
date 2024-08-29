package com.dnd.gongmuin.chat.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.chat.dto.ChatMapper;
import com.dnd.gongmuin.chat.dto.ChatMessageRequest;
import com.dnd.gongmuin.chat.dto.ChatMessageResponse;
import com.dnd.gongmuin.chat.repository.ChatMessageRepository;
import com.dnd.gongmuin.common.dto.PageMapper;
import com.dnd.gongmuin.common.dto.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

	private final ChatMessageRepository chatMessageRepository;

	@Transactional
	public void saveChatMessage(
		ChatMessageRequest request,
		Long chatRoomId,
		Long memberId
	) {
		chatMessageRepository.save(ChatMapper.toChatMessage(request, chatRoomId, memberId));
	}

	@Transactional(readOnly = true)
	public PageResponse<ChatMessageResponse> getChatMessage(Long chatRoomId, Pageable pageable) {
		Slice<ChatMessageResponse> responsePage = chatMessageRepository
			.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId, pageable)
			.map(ChatMapper::toChatMessageResponse);
		return PageMapper.toPageResponse(responsePage);
	}

}
