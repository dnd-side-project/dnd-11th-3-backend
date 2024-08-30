package com.dnd.gongmuin.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.member.domain.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustom {
	Optional<Member> findBySocialEmail(String socialEmail);

	boolean existsByNickname(String nickname);

	boolean existsByOfficialEmail(String officialEmail);

	boolean existsBySocialEmail(String socialEmail);

	Member findByOfficialEmail(String officialEmail);
}
