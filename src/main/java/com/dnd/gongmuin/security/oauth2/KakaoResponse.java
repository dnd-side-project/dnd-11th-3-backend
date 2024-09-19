package com.dnd.gongmuin.security.oauth2;

import java.util.Map;

import com.dnd.gongmuin.member.domain.Provider;

public class KakaoResponse implements Oauth2Response {

	private final Map<String, Object> attribute;
	private final Long id;

	public KakaoResponse(Map<String, Object> attribute) {
		this.attribute = (Map<String, Object>)attribute.get("kakao_account");
		this.id = (Long)attribute.get("id");
	}

	@Override
	public String getProvider() {
		return Provider.KAKAO.getLabel();
	}

	@Override
	public String getProviderId() {
		return this.id.toString();
	}

	@Override
	public String getEmail() {
		return attribute.get("email").toString();
	}

	@Override
	public String getName() {
		return ((Map<String, Object>)attribute.get("profile")).get("nickname").toString();
	}

	@Override
	public String createSocialEmail() {
		return String.format("%s%s/%s",
			this.getProvider(),
			this.getProviderId(),
			this.getEmail()
		);
	}

}
