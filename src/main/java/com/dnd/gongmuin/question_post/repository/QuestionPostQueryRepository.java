package com.dnd.gongmuin.question_post.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.dnd.gongmuin.question_post.dto.request.QuestionPostSearchCondition;
import com.dnd.gongmuin.question_post.dto.response.QuestionPostSimpleResponse;

public interface QuestionPostQueryRepository {
	Slice<QuestionPostSimpleResponse> searchQuestionPosts(QuestionPostSearchCondition condition, Pageable pageable);
}
