package com.dnd.gongmuin.question_post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.domain.QuestionPostImage;

public interface QuestionPostImageRepository extends JpaRepository<QuestionPostImage, Long> {
	void deleteByQuestionPost(QuestionPost questionPost);
}
