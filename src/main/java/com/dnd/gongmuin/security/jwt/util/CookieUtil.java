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

	// private final CookieConfig cookieConfig;
	//
	// public Cookie createCookie(String token) {
	// 	return cookieConfig.createCookie(token);
	// }

	public Cookie createCookie(String token) {
		Cookie cookie = new Cookie("Authorization", token);
		cookie.setPath("/");
		cookie.setDomain(".gongmuin.site");
		cookie.setMaxAge(60 * 60);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "Strict");
		return cookie;
	}

	public String getCookieValue(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		log.info("=============cookies 탐색===================");
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("Authorization".equals(cookie.getName())) {
					log.info("===============cookie.getValue() : {} =====================", cookie.getValue());
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public void deleteCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("Authorization", null);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");

		response.addCookie(cookie);
	}
}
