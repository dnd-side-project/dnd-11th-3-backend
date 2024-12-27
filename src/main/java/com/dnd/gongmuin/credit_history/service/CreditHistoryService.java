package com.dnd.gongmuin.credit_history.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dnd.gongmuin.credit_history.domain.CreditHistory;
import com.dnd.gongmuin.credit_history.domain.CreditType;
import com.dnd.gongmuin.credit_history.dto.CreditHistoryMapper;
import com.dnd.gongmuin.credit_history.repository.CreditHistoryRepository;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditHistoryService {

	private final CreditHistoryRepository creditHistoryRepository;
	private final MemberRepository memberRepository;

	public void saveCreditHistory(CreditType creditType, int credit, Member member) {
		creditHistoryRepository.save(
			CreditHistoryMapper.toCreditHistory(creditType, credit, member)
		);
	}

	public void saveCreditHistoryInMemberIds(List<Long> memberIds, CreditType type, int credit) {
		List<Member> inquirers = memberRepository.findAllById(memberIds);
		List<CreditHistory> histories = inquirers.stream()
			.map(inquirer -> CreditHistory.of(type, credit, inquirer))
			.toList();
		creditHistoryRepository.saveAll(histories);
	}
}
