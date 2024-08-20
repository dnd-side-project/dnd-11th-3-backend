package com.dnd.gongmuin.security.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dnd.gongmuin.security.handler.CustomAccessDeniedHandler;
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
	private final CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
					.requestMatchers("/api/auth/reissue/token").permitAll()
					.requestMatchers(
						"/api/auth/member",
						"/api/auth/check-nickname",
						"/api/auth/check-email",
						"/api/auth/check-email/authCode"
					).hasAnyAuthority("ROLE_GUEST", "ROLE_USER")
					.anyRequest().hasAuthority("ROLE_USER")
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
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
				.accessDeniedHandler(customAccessDeniedHandler)
			);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
			.requestMatchers(
				"/error", "/favicon.ico", "/api/auth/tempSignUP", "/api/auth/tempSignIn",
				"/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html"
			);
	}

	// Spring Security cors Bean 등록
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://gongmuin.netlify.app/",
			"https://gongmuin.site/", "http://localhost:8080"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
		configuration.setMaxAge(3000L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
