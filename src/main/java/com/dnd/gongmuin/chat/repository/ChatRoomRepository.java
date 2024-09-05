package com.dnd.gongmuin.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.chat.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
