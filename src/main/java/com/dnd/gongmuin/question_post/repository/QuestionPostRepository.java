package com.dnd.gongmuin.question_post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.question_post.domain.QuestionPost;

public interface QuestionPostRepository extends JpaRepository<QuestionPost, Long> {
	boolean existsById(Long id);
}