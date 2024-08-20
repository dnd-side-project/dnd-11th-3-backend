package com.dnd.gongmuin.security.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOauth2User implements OAuth2User {

	private final AuthInfo authInfo;
	private Map<String, Object> attributes;

	public CustomOauth2User(AuthInfo authInfo) {
		this.authInfo = authInfo;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String role = authInfo.getRole();
		return Collections.singletonList(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getName() {
		return authInfo.getSocialName();
	}

	public String getEmail() {
		return authInfo.getSocialEmail();
	}
}
