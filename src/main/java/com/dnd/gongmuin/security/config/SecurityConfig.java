package com.dnd.gongmuin.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dnd.gongmuin.security.handler.CustomAuthenticationEntryPoint;
import com.dnd.gongmuin.security.handler.CustomOauth2FailureHandler;
import com.dnd.gongmuin.security.handler.CustomOauth2SuccessHandler;
import com.dnd.gongmuin.security.jwt.util.TokenAuthenticationFilter;
import com.dnd.gongmuin.security.jwt.util.TokenExceptionFilter;
import com.dnd.gongmuin.security.service.CustomOauth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOauth2UserService customOauth2UserService;
	private final CustomOauth2SuccessHandler customOauth2SuccessHandler;
	private final CustomOauth2FailureHandler customOauth2FailureHandler;
	private final TokenAuthenticationFilter tokenAuthenticationFilter;

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers("/error", "/favicon.ico");
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors((auth) -> auth.disable())
			.csrf((auth) -> auth.disable())
			.formLogin((auth) -> auth.disable())
			.httpBasic((auth) -> auth.disable())
			.sessionManagement(
				(session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);

		http
			.authorizeHttpRequests(
				(auth) -> auth
					.requestMatchers("/").permitAll()
					.requestMatchers("/api/**").permitAll()
					.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
					.requestMatchers("/api/auth/signin/kakao").permitAll()
					.requestMatchers("/api/auth/member").permitAll()
					.requestMatchers("/api/auth/check-email").permitAll()
					.requestMatchers("/additional-info").permitAll()
					.anyRequest().authenticated()
			)
			.oauth2Login((oauth2) -> oauth2
				.userInfoEndpoint(
					(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOauth2UserService))
				)
				.successHandler(customOauth2SuccessHandler)
				.failureHandler(customOauth2FailureHandler)
			)

			.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass())

			.exceptionHandling((exception) -> exception
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

		return http.build();
	}
}
