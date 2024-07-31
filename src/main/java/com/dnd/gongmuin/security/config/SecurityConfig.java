package com.dnd.gongmuin.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.dnd.gongmuin.security.handler.CustomOauth2AuthenticationSuccessHandler;
import com.dnd.gongmuin.security.service.CustomOauth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOauth2UserService customOauth2UserService;
	private final CustomOauth2AuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf((auth) -> auth.disable())
			.formLogin((auth) -> auth.disable())
			.httpBasic((auth) -> auth.disable());
		http
			// JWT 토큰 인증 기반을 위한 Session 정책 설정
			.sessionManagement(
				(session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
		http
			.authorizeHttpRequests(
				(auth) -> auth
					.requestMatchers("/login/kakao").permitAll()
					.requestMatchers("/additional-info").permitAll()
					.requestMatchers("/").permitAll()
					.anyRequest().authenticated()
			);
		http
			.oauth2Login((oauth2) -> oauth2
				.successHandler(customOauth2AuthenticationSuccessHandler) // 로그인 성공 후 리디렉션할 URL
				.failureUrl("/") // 로그인 실패 후 리디렉션할 URL
				.userInfoEndpoint(
					(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService))
				)
			);

		return http.build();
	}
}
