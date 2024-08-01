package com.dnd.gongmuin.security.handler;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.dnd.gongmuin.auth.service.AuthService;
import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.security.dto.CustomOauth2User;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler implements AuthenticationSuccessHandler {

	private final AuthService authService;
	private final MemberRepository memberRepository;
	private final TokenProvider tokenProvider;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOauth2User customOauth2User = (CustomOauth2User)authentication.getPrincipal();

		String socialEmail = customOauth2User.getEmail();
		Member findmember = memberRepository.findBySocialEmail(socialEmail)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));

		String token = tokenProvider.generateSignUpToken(customOauth2User, new Date());
		response.setHeader("Authorization", token);

		if (!isAuthStatusOld(findmember)) {
			response.sendRedirect("/additional-info");
		} else {
			response.sendRedirect("/");
		}
	}

	private boolean isAuthStatusOld(Member member) {
		return authService.isAuthStatusOld(member);
	}

}
