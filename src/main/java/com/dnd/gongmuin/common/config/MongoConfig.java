package com.dnd.gongmuin.common.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.dnd.gongmuin.converter.DateToLocalDateTimeKstConverter;
import com.dnd.gongmuin.converter.LocalDateTimeToDateKstConverter;

@Configuration
public class MongoConfig {

	@Bean
	public MongoCustomConversions customConversions(
		LocalDateTimeToDateKstConverter localDateTimeToDateKstConverter,
		DateToLocalDateTimeKstConverter dateToLocalDateTimeKstConverter) {

		return new MongoCustomConversions(Arrays.asList(
			localDateTimeToDateKstConverter,
			dateToLocalDateTimeKstConverter
		));
	}
}