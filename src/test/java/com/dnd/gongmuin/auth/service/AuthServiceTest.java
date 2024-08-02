package com.dnd.gongmuin.auth.service;

import static com.dnd.gongmuin.auth.domain.AuthStatus.*;
import static com.dnd.gongmuin.member.domain.JobCategory.*;
import static com.dnd.gongmuin.member.domain.JobGroup.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.gongmuin.auth.domain.Auth;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.repository.MemberRepository;

@Transactional
@SpringBootTest
class AuthServiceTest {

	@Autowired
	AuthService authService;

	@Autowired
	MemberRepository memberRepository;

	@DisplayName("신규 회원의 상태는 Old가 아니다.")
	@Test
	void isAuthStatusOld() {
		// given
		Member member = createMember();
		Member savedMember = memberRepository.save(member);
		authService.saveOrUpdate(savedMember);

		// when
		boolean result = authService.isAuthStatusOld(savedMember);

		// then
		assertThat(result).isFalse();
	}

	@DisplayName("신규 회원의 공무원 이메일 값이 있다면 Auth 상태는 OLD로 업데이트된다.")
	@Test
	void updateStatusWithOfficialEmail() {
		// given
		Member member = createMember();
		Member savedMember = memberRepository.save(member);
		Auth newAuth = authService.saveOrUpdate(savedMember);
		Member reLoginMember = newAuth.getMember();

		// when
		Auth oldAuth = authService.saveOrUpdate(reLoginMember);

		// then
		assertThat(oldAuth.getStatus()).isEqualTo(OLD);
	}

	@DisplayName("신규 회원의 공무원 이메일 값이 없다면 Auth 상태는 NEW로 유지된다.")
	@Test
	void maintainStatusWithOfficialEmail() {
		// given
		Member member = Member.of("김신규", "KAKAO123/newMember@member.com", 1000);
		Member savedMember = memberRepository.save(member);
		Auth newAuth = authService.saveOrUpdate(savedMember);
		Member reLoginMember = newAuth.getMember();

		// when
		Auth oldAuth = authService.saveOrUpdate(reLoginMember);

		// then
		assertThat(oldAuth.getStatus()).isEqualTo(NEW);
	}

	private Member createMember() {
		return Member.builder()
			.nickname("김철수")
			.socialName("철수")
			.socialEmail("KAKAO123/abc@naver.com")
			.officialEmail("abc123@korea.com")
			.jobCategory(GAS)
			.jobGroup(ENGINEERING)
			.credit(10000)
			.build();

	}

}