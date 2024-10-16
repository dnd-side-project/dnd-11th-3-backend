package com.dnd.gongmuin.security.oauth2;

import java.util.Map;

import com.dnd.gongmuin.member.domain.Provider;

public class NaverResponse implements Oauth2Response {

	private final Map<String, Object> attribute;
	private final String oauth2AccessToken;

	public NaverResponse(Map<String, Object> attribute, String oauth2AccessToken) {
		this.attribute = (Map<String, Object>)attribute.get("response");
		this.oauth2AccessToken = oauth2AccessToken;
	}

	@Override
	public String getProvider() {
		return Provider.NAVER.getLabel();
	}

	@Override
	public String getProviderId() {
		return this.attribute.get("id").toString();
	}

	@Override
	public String getEmail() {
		return this.attribute.get("email").toString();
	}

	@Override
	public String getName() {
		return this.attribute.get("name").toString();
	}

	@Override
	public String createSocialEmail() {
		return String.format("%s%s/%s",
			this.getProvider(),
			this.getProviderId(),
			this.getEmail()
		);
	}

	@Override
	public String getOauth2AccessToken() {
		return this.oauth2AccessToken;
	}
}
