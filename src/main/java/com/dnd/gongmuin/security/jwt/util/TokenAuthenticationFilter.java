package com.dnd.gongmuin.security.jwt.util;

import static org.springframework.http.HttpHeaders.*;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dnd.gongmuin.redis.util.RedisUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private static final String TOKEN_PREFIX = "Bearer ";
	private final TokenProvider tokenProvider;
	private final RedisUtil redisUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = resolveToken(request);

		if (tokenProvider.validateToken(accessToken, new Date())) {
			// accessToken logout 여부 확인
			String isLogout = redisUtil.getValues(accessToken);
			if ("false".equals(isLogout)) {
				saveAuthentication(accessToken);
			}
		}

		filterChain.doFilter(request, response);
	}

	private void saveAuthentication(String accessToken) {
		Authentication authentication = tokenProvider.getAuthentication(accessToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String resolveToken(HttpServletRequest request) {
		String token = request.getHeader(AUTHORIZATION);
		if (ObjectUtils.isEmpty(token) || !token.startsWith(TOKEN_PREFIX)) {
			return null;
		}
		return token.substring(TOKEN_PREFIX.length());
	}
}
