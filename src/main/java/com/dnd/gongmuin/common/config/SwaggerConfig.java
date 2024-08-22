package com.dnd.gongmuin.common.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
	info = @Info(
		title = "GongmuIn API",
		description = "공무인 API 명세서",
		version = "v.1.0"),
	servers = {
		@Server(url = "https://gongmuin.site", description = "Deploy Server URL"),
		@Server(url = "http://localhost:8080", description = "Local Host URL")
	}
)
@Configuration
public class SwaggerConfig {

	@Bean
	public GroupedOpenApi chatOpenApi() {
		String[] paths = {"/api/**"};

		return GroupedOpenApi.builder()
			.group("API v.1.0")
			.pathsToMatch(paths)
			.build();
	}
}
