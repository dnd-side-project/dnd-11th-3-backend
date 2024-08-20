package com.dnd.gongmuin.common.fixture;

import com.dnd.gongmuin.auth.domain.Auth;
import com.dnd.gongmuin.auth.domain.AuthStatus;
import com.dnd.gongmuin.auth.domain.Provider;
import com.dnd.gongmuin.member.domain.Member;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthFixture {

	public static Auth auth(Member member) {
		return Auth.of(
			Provider.KAKAO,
			AuthStatus.NEW,
			member
		);
	}
}
