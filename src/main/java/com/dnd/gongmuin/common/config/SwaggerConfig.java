package com.dnd.gongmuin.common.config;

import java.util.List;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

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
	public OpenAPI openAPI() {
		SecurityScheme securityScheme = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.in(SecurityScheme.In.HEADER)
			.name(HttpHeaders.AUTHORIZATION);
		SecurityRequirement securityRequirement
			= new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION);

		return new OpenAPI()
			.components(new Components().addSecuritySchemes(HttpHeaders.AUTHORIZATION, securityScheme))
			.security(List.of(securityRequirement));
	}

	@Bean
	public GroupedOpenApi chatOpenApi() {
		String[] paths = {"/api/**"};

		return GroupedOpenApi.builder()
			.group("API v.1.0")
			.pathsToMatch(paths)
			.build();
	}
}
