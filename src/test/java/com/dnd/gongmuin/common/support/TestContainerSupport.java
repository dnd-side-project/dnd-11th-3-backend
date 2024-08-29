package com.dnd.gongmuin.common.support;

import static lombok.AccessLevel.*;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
public abstract class TestContainerSupport {

	private static final String MONGO_IMAGE = "mongo:latest";
	private static final String REDIS_IMAGE = "redis:latest";
	private static final String MYSQL_IMAGE = "mysql:8.0";
	private static final int MONGO_PORT = 27017;
	private static final int REDIS_PORT = 6379;
	private static final int MYSQL_PORT = 3306;

	private static final MongoDBContainer MONGO;

	private static final GenericContainer<?> REDIS;
	private static final JdbcDatabaseContainer<?> MYSQL;

	// 컨테이너 싱글톤으로 생성
	static {
		MONGO = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE))
			.withExposedPorts(MONGO_PORT)
			.withReuse(true);
		REDIS = new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
			.withExposedPorts(REDIS_PORT)
			.withReuse(true);
		MYSQL = new MySQLContainer<>(DockerImageName.parse(MYSQL_IMAGE))
			.withExposedPorts(MYSQL_PORT)
			.withReuse(true);

		MONGO.start();
		REDIS.start();
		MYSQL.start();
	}

	// 동적으로 속성 할당
	@DynamicPropertySource
	public static void setUp(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.host", MONGO::getHost);
		registry.add("spring.data.mongodb.port", () -> String.valueOf(MONGO.getMappedPort(MONGO_PORT)));

		registry.add("spring.data.redis.host", REDIS::getHost);
		registry.add("spring.data.redis.port", () -> String.valueOf(REDIS.getMappedPort(REDIS_PORT)));

		registry.add("spring.datasource.driver-class-name", MYSQL::getDriverClassName);
		registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
		registry.add("spring.datasource.username", MYSQL::getUsername);
		registry.add("spring.datasource.password", MYSQL::getPassword);
	}
}
