package com.dnd.gongmuin.chatroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.chatroom.domain.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomQueryRepository {
}
