package com.dnd.gongmuin.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.answer.domain.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
