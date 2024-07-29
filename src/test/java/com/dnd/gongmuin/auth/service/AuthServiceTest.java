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

	@DisplayName("회원가입 이후 신규 회원의 Auth 상태는 Old로 업데이트 된다.")
	@Test
	void saveOrUpdate() {
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

	private Member createMember() {
		return Member.builder()
			.nickname("김철수")
			.socialName("KAKAO123/철수")
			.socialEmail("abc@naver.com")
			.officialEmail("abc123@korea.com")
			.jobCategory(GAS)
			.jobGroup(ENGINEERING)
			.credit(10000)
			.build();

	}

}