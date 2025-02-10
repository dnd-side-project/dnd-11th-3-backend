package com.dnd.gongmuin.security.jwt.util;

import org.springframework.context.annotation.Profile;

import jakarta.servlet.http.Cookie;

@Profile("dev")
public class DevCookie implements CookieConfig {
	@Override
	public Cookie createCookie(String token) {
		Cookie cookie = new Cookie("Authorization", token);
		cookie.setPath("/");
		cookie.setDomain("gongmuin.site");
		cookie.setMaxAge(60 * 60);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setAttribute("SameSite", "Strict");
		return cookie;
	}
}
