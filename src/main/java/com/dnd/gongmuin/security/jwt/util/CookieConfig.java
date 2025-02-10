package com.dnd.gongmuin.security.jwt.util;

import jakarta.servlet.http.Cookie;

public interface CookieConfig {

	public Cookie createCookie(String token);
}
