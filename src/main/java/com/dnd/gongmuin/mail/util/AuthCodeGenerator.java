package com.dnd.gongmuin.mail.util;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class AuthCodeGenerator {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static final int CODE_LENGTH = 6;

	public String createAuthCode() {
		return Stream.generate(() -> RANDOM.nextInt(10))
			.limit(CODE_LENGTH)
			.map(String::valueOf)
			.collect(Collectors.joining());
	}
}
