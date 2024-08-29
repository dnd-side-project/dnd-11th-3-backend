package com.dnd.gongmuin.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.chat.domain.ChatMessage;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
	Slice<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(long chatRoomId, Pageable pageable);
}
