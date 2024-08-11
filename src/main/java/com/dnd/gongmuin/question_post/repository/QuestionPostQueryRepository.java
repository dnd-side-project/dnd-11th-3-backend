package com.dnd.gongmuin.question_post.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.question_post.domain.QuestionPost;
import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;

public interface QuestionPostQueryRepository {
	Slice<QuestionPost> searchQuestionPosts(QuestionPostSearchCondition condition, Pageable pageable);
}
