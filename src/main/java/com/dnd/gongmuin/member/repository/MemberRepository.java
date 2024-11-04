package com.dnd.gongmuin.member.repository;

import com.dnd.gongmuin.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustom {
    Optional<Member> findBySocialEmail(String socialEmail);

    boolean existsByNickname(String nickname);

    boolean existsByOfficialEmail(String officialEmail);

    boolean existsBySocialEmail(String socialEmail);

    Member findByOfficialEmail(String officialEmail);

    Optional<Member> findByRole(String role);

}
