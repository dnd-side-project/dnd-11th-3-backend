package com.dnd.gongmuin.answer.repository;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.answer.domain.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

	Slice<Answer> findByQuestionPostId(Long questionPostId);
}
