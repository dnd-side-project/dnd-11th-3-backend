package com.dnd.gongmuin.security.handler;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.dnd.gongmuin.common.exception.ErrorResponse;
import com.dnd.gongmuin.security.exception.SecurityErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private static final String ROLE_GUEST = "ROLE_GUEST";
	private static final String ROLE_USER = "ROLE_USER";

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
		AccessDeniedException accessDeniedException) throws IOException, ServletException {
		// 현재 인증된 사용자 정보 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// 사용자 권한에 따라 다른 응답 제공
		if (!Objects.isNull(accessDeniedException)) {
			if (!matchAuthenticationFromRole(authentication, ROLE_USER)) {
				// ROLE_USER 권한이 없는 경우
				setUpResponse(response, SecurityErrorCode.FORBIDDEN_USER);
			} else if (!matchAuthenticationFromRole(authentication, ROLE_GUEST)) {
				// ROLE_GUEST 권한이 없는 경우
				setUpResponse(response, SecurityErrorCode.FORBIDDEN_GUEST);
			} else {
				// 기타 권한이 없는 경우
				setUpResponse(response, SecurityErrorCode.FORBIDDEN_MISMATCH);
			}
		} else {
			setUpResponse(response, SecurityErrorCode.FORBIDDEN_MISMATCH);
		}
	}

	private static boolean matchAuthenticationFromRole(Authentication authentication, String role) {
		String authRole = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining());

		return Objects.equals(authRole, role);
	}

	private static void setUpResponse(
		HttpServletResponse response,
		SecurityErrorCode securityErrorCode
	) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 Unauthorized
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ErrorResponse errorResponse = new ErrorResponse(
			securityErrorCode.getMessage(),
			securityErrorCode.getCode()
		);

		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = mapper.writeValueAsString(errorResponse);

		response.getWriter().write(jsonResponse);
	}
}
