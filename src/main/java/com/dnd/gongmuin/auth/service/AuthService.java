package com.dnd.gongmuin.auth.service;

import static com.dnd.gongmuin.auth.domain.AuthStatus.*;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.dnd.gongmuin.auth.domain.Auth;
import com.dnd.gongmuin.auth.domain.Provider;
import com.dnd.gongmuin.auth.exception.AuthErrorCode;
import com.dnd.gongmuin.auth.repository.AuthRepository;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthRepository authRepository;
	private final MemberService memberService;

	public Auth saveOrUpdate(Member savedMember) {
		Auth findedOrCreatedAuth = authRepository.findByMember(savedMember)
			.map(a -> a.updateStatus())
			.orElse(createAuth(savedMember));

		return authRepository.save(findedOrCreatedAuth);
	}

	public boolean isAuthStatusOld(Member member) {
		Auth findAuth = authRepository.findByMember(member)
			.orElseThrow(() -> new NotFoundException(AuthErrorCode.NOT_FOUND_AUTH));

		return Objects.equals(findAuth.getStatus(), OLD);
	}

	private Auth createAuth(Member savedMember) {
		String providerName = memberService.parseProviderFromSocialName(savedMember);
		Provider provider = Provider.fromProviderName(providerName);

		return Auth.builder()
			.status(NEW)
			.provider(provider)
			.build();
	}

}
