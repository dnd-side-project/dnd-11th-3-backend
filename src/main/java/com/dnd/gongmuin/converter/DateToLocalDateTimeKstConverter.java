package com.dnd.gongmuin.converter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class DateToLocalDateTimeKstConverter implements Converter<Date, LocalDateTime> {

	@Override
	public LocalDateTime convert(Date source) {
		return source.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().minusHours(9);
	}
}
