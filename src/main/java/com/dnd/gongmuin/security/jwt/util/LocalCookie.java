package com.dnd.gongmuin.security.jwt.util;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Profile("local")
public class LocalCookie implements CookieConfig {

	@Override
	public Cookie createCookie(String token) {
		Cookie cookie = new Cookie("Authorization", token);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 180);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "None");
		return cookie;
	}

	@Override
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
