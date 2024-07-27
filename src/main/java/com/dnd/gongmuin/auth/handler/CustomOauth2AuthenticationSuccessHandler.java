package com.dnd.gongmuin.auth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.dnd.gongmuin.auth.dto.CustomOauth2User;
import com.dnd.gongmuin.auth.service.AuthService;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final AuthService authService;
	private final MemberRepository memberRepository;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOauth2User customOauth2User = (CustomOauth2User)authentication.getPrincipal();

		String socialEmail = customOauth2User.getEmail();
		Member findmember = memberRepository.findBySocialEmail(socialEmail)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));

		if (!isAuthStatusOld(findmember)) {
			response.sendRedirect("/additional-info");
		} else {
			// TODO : 기존회원 JWT 발급 및 페이지 리다이렉션 구현
			response.sendRedirect("/");
		}
	}

	private boolean isAuthStatusOld(Member member) {
		return authService.isAuthStatusOld(member);
	}

}
