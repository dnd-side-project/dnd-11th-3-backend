package com.dnd.gongmuin.security.jwt.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CookieUtil {

	private final CookieConfig cookieConfig;

	public Cookie createCookie(String token) {
		return cookieConfig.createCookie(token);
	}

	public void deleteCookie(HttpServletResponse response) {
		cookieConfig.deleteCookie(response);
	}

	public String getCookieValue(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("Authorization".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
