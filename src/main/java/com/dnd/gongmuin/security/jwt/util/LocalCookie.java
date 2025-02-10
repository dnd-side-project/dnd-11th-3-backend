package com.dnd.gongmuin.security.jwt.util;

import org.springframework.context.annotation.Profile;

import jakarta.servlet.http.Cookie;

@Profile("local")
public class LocalCookie implements CookieConfig {

	@Override
	public Cookie createCookie(String token) {
		Cookie cookie = new Cookie("Authorization", token);
		cookie.setPath("/");
		cookie.setMaxAge(60 * 60);
		cookie.setHttpOnly(false);
		cookie.setSecure(false);
		cookie.setAttribute("SameSite", "None");
		return cookie;
	}
}
