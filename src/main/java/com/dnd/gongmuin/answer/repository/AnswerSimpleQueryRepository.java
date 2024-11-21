package com.dnd.gongmuin.answer.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.answer.domain.Answer;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AnswerSimpleQueryRepository {

	private final EntityManager em;

	public Optional<Answer> findAnswerById(Long answerId) {
		Answer answer = em.createQuery(
				"select a from Answer a" +
					" join fetch a.member m" +
					" where a.id = :answerId", Answer.class
			)
			.setParameter("answerId", answerId)
			.getSingleResult();
		return Optional.of(answer);
	}
}
