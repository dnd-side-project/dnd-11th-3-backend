package com.dnd.gongmuin.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.gongmuin.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findBySocialEmail(String socialEmail);

	boolean existsByNickname(String nickname);

	boolean existsByOfficialEmail(String officialEmail);

	boolean existsBySocialEmail(String socialEmail);

	Member findByOfficialEmail(String officialEmail);
}
