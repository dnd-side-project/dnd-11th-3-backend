package com.dnd.gongmuin.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	// Spring MVC cors 설정
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			// .allowedOrigins("http://localhost:3000", "https://gongmuin.netlify.app",
			// 	"https://gongmuin.site", "http://localhost:8080")
			.allowedOrigins("*")
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
			.allowCredentials(true)
			.allowedHeaders("*")
			.exposedHeaders("Set-Cookie", "Authorization")
			.maxAge(3000);
	}
}
