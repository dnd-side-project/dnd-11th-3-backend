package com.dnd.gongmuin.security.jwt.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieConfig {

	public Cookie createCookie(String token);

	public void deleteCookie(HttpServletResponse response);
}
