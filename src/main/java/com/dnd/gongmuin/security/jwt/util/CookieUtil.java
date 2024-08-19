package com.dnd.gongmuin.security.jwt.util;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CookieUtil {

	public Cookie createCookie(String token) {
		Cookie cookie = new Cookie("Authorization", token);
		cookie.setPath("/");
		cookie.setMaxAge(1000 * 60 * 60);
		cookie.setHttpOnly(true);
		return cookie;
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
