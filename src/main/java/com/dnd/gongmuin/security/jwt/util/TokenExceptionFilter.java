package com.dnd.gongmuin.security.jwt.util;

import static org.springframework.http.HttpStatus.*;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import com.dnd.gongmuin.common.exception.runtime.CustomJwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenExceptionFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (CustomJwtException e) {
			response.sendError(NOT_FOUND.value(), e.getMessage());
		}
	}
}
