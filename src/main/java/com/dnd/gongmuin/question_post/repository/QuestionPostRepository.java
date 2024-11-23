package com.dnd.gongmuin.question_post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.question_post.domain.QuestionPost;

@Repository
public interface QuestionPostRepository extends JpaRepository<QuestionPost, Long>, QuestionPostQueryRepository {
	boolean existsById(Long id);

	List<QuestionPost> findAllByMember(Member member);

	@Query("select q from QuestionPost q "
		+ "join fetch q.member where q.id = :questionPostId")
	Optional<QuestionPost> findByIdWithMember(Long questionPostId);

	@Modifying(flushAutomatically = true, clearAutomatically = true)
	@Query("UPDATE QuestionPost q SET q.member = :member WHERE q.member.id = :memberId")
	void updateQuestionPostsMember(Long memberId, Member member);
}