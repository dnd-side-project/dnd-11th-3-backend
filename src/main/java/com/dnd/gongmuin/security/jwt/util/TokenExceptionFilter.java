package com.dnd.gongmuin.security.jwt.util;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.dnd.gongmuin.common.exception.ErrorResponse;
import com.dnd.gongmuin.common.exception.runtime.CustomJwtException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (CustomJwtException e) {

			log.error("JWT 검증 실패로 인한 예외 발생 : {}", e.getMessage());

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getCode());

			ObjectMapper mapper = new ObjectMapper();
			String jsonResponse = mapper.writeValueAsString(errorResponse);

			response.getWriter().write(jsonResponse);
		}
	}
}
