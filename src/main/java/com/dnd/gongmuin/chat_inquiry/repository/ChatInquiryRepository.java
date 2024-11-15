package com.dnd.gongmuin.chat_inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;

public interface ChatInquiryRepository extends JpaRepository<ChatInquiry, Long>, ChatInquiryQueryRepository {
}
