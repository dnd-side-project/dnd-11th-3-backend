package com.dnd.gongmuin.security.oauth2;

public interface Oauth2Response {
	String getProvider();

	String getProviderId();

	String getEmail();

	String getName();

	String createSocialEmail();

	String getOauth2AccessToken();
}
