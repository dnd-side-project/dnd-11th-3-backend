package com.dnd.gongmuin.mail.dto;

import com.dnd.gongmuin.mail.dto.response.AuthCodeResponse;
import com.dnd.gongmuin.mail.dto.response.SendMailResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailMapper {

	public static SendMailResponse toSendMailResponse(String targetEmail) {
		return new SendMailResponse(targetEmail);
	}

	public static AuthCodeResponse toAuthCodeResponse(boolean result) {
		return new AuthCodeResponse(result);
	}
}
