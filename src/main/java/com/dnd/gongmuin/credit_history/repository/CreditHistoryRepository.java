package com.dnd.gongmuin.credit_history.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.credit_history.domain.CreditHistory;

@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {
}
