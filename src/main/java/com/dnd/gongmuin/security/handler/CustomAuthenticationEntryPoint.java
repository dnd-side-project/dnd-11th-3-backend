package com.dnd.gongmuin.security.handler;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.dnd.gongmuin.common.exception.ErrorResponse;
import com.dnd.gongmuin.security.exception.SecurityErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		log.error("비인가 사용자 요청 -> 예외 발생", authException.getStackTrace());

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ErrorResponse errorResponse = new ErrorResponse(SecurityErrorCode.UNAUTHORIZED.getMessage(),
			SecurityErrorCode.UNAUTHORIZED.getCode());

		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = mapper.writeValueAsString(errorResponse);

		response.getWriter().write(jsonResponse);
	}
}
