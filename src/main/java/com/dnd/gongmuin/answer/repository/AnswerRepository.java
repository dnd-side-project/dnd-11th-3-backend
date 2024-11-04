package com.dnd.gongmuin.answer.repository;

import com.dnd.gongmuin.answer.domain.Answer;
import com.dnd.gongmuin.member.domain.Member;
import java.util.List;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Slice<Answer> findByQuestionPostId(Long questionPostId);

    List<Answer> findAllByMember(Member member);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Answer a SET a.member = :member WHERE a.member.id = :memberId")
    public void updateAnswersMember(Long memberId, Member member);
}