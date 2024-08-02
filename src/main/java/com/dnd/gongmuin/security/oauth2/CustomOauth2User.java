package com.dnd.gongmuin.security.oauth2;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.dnd.gongmuin.auth.dto.AuthDto;

public class CustomOauth2User implements OAuth2User {

	private final AuthDto authDto;
	private Map<String, Object> attributes;

	public CustomOauth2User(AuthDto authDto) {
		this.authDto = authDto;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String role = "ROLE_USER";
		return Collections.singletonList(new SimpleGrantedAuthority(role));
	}

	@Override
	public String getName() {
		return authDto.getSocialName();
	}

	public String getEmail() {
		return authDto.getSocialEmail();
	}
}
