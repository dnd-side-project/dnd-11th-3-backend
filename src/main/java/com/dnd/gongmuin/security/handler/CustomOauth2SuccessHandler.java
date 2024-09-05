package com.dnd.gongmuin.security.handler;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.dnd.gongmuin.common.exception.runtime.NotFoundException;
import com.dnd.gongmuin.member.domain.Member;
import com.dnd.gongmuin.member.exception.MemberErrorCode;
import com.dnd.gongmuin.member.repository.MemberRepository;
import com.dnd.gongmuin.security.jwt.util.CookieUtil;
import com.dnd.gongmuin.security.jwt.util.TokenProvider;
import com.dnd.gongmuin.security.oauth2.CustomOauth2User;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler implements AuthenticationSuccessHandler {

	private final MemberRepository memberRepository;
	private final TokenProvider tokenProvider;
	private final CookieUtil cookieUtil;
	@Value("${direct.sign-up}")
	private String REDIRECTION_SIGNUP;
	@Value("${direct.home}")
	private String REDIRECTION_HOME;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOauth2User customOauth2User = (CustomOauth2User)authentication.getPrincipal();

		String socialEmail = customOauth2User.getEmail();
		Member findmember = memberRepository.findBySocialEmail(socialEmail)
			.orElseThrow(() -> new NotFoundException(MemberErrorCode.NOT_FOUND_MEMBER));

		String token = tokenProvider.generateAccessToken(customOauth2User, new Date());
		tokenProvider.generateRefreshToken(customOauth2User, new Date());

		response.addCookie(cookieUtil.createCookie(token));

		if (isRoleGuest(findmember.getRole())) {
			response.sendRedirect(REDIRECTION_SIGNUP);
		} else {
			response.sendRedirect(REDIRECTION_HOME);
		}
	}

	private boolean isRoleGuest(String role) {
		return "ROLE_GUEST".equals(role);
	}
}
