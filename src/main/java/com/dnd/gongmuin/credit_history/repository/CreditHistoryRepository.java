package com.dnd.gongmuin.credit_history.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.dnd.gongmuin.member.domain.Member;

@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {
	void deleteByMember(Member member);

	List<CreditHistory> findAllByMember(Member loginMember);
}
