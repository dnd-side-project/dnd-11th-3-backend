package com.dnd.gongmuin.mail.dto;

import com.dnd.gongmuin.mail.dto.response.SendMailResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailMapper {

	public static SendMailResponse toSendMailResponse(String toEmail) {
		return new SendMailResponse(toEmail);
	}
}
