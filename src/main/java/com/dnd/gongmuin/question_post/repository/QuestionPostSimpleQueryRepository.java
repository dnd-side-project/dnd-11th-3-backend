package com.dnd.gongmuin.question_post.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.question_post.domain.QuestionPost;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QuestionPostSimpleQueryRepository {

	private final EntityManager em;

	public Optional<QuestionPost> findQuestionPostById(Long questionPostId) {
		QuestionPost questionPost = em.createQuery(
				"select q from QuestionPost q" +
					" join fetch q.member m" +
					" where q.id = :questionPostId", QuestionPost.class
			)
			.setParameter("questionPostId", questionPostId)
			.getSingleResult();
		return Optional.of(questionPost);
	}
}
