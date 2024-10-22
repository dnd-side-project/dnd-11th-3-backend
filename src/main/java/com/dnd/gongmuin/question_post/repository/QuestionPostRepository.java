package com.dnd.gongmuin.question_post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

@Repository
public interface QuestionPostRepository extends JpaRepository<QuestionPost, Long>, QuestionPostQueryRepository {
	boolean existsById(Long id);

	List<QuestionPost> findAllByMember(Member member);
}