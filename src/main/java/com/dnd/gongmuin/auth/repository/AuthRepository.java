package com.dnd.gongmuin.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dnd.gongmuin.auth.domain.Auth;
import com.dnd.gongmuin.member.domain.Member;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {

	Optional<Auth> findByMember(Member member);
}
