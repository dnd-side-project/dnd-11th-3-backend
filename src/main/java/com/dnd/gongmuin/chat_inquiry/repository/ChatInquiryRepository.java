package com.dnd.gongmuin.chat_inquiry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.chat_inquiry.domain.ChatInquiry;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

public interface ChatInquiryRepository extends JpaRepository<ChatInquiry, Long>, ChatInquiryQueryRepository {
	boolean existsByInquirerAndAnswererAndQuestionPost(Member inquirer, Member answerer, QuestionPost questionPost);
}
