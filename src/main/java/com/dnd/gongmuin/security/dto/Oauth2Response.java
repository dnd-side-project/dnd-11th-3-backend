package com.dnd.gongmuin.security.dto;

public interface Oauth2Response {
	String getProvider();

	String getProviderId();

	String getEmail();

	String getName();

	String createSocialEmail();

}
