package com.dnd.gongmuin.credit_history.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.credit_history.CreditHistory;

public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {
}