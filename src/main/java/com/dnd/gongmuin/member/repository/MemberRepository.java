package com.dnd.gongmuin.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
