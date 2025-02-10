package com.dnd.gongmuin.security.jwt.util;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;

@Component
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
