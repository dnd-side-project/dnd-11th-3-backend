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
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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

		String token = tokenProvider.generateAccessToken(customOauth2User, new Date());
		tokenProvider.generateRefreshToken(customOauth2User, new Date());

		response.addCookie(createCookie(token));

		if (!isAuthStatusOld(findmember)) {
			response.sendRedirect("http://localhost:3000/signup");
		} else {
			response.sendRedirect("http://localhost:3000/home");
		}
	}

	private boolean isAuthStatusOld(Member member) {
		return authService.isAuthStatusOld(member);
	}

	private static Cookie createCookie(String token) {
		Cookie cookie = new Cookie("Authorization", token);
		cookie.setPath("/");
		cookie.setMaxAge(1000 * 60 * 60);
		cookie.setHttpOnly(true);
		return cookie;
	}

}
