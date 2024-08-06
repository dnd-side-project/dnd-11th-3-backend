package com.dnd.gongmuin.common.dto;

import static lombok.AccessLevel.*;

import org.springframework.data.domain.Slice;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class PageMapper {

	public static <T> PageResponse<T> toPageResponse(Slice<T> page) {
		return new PageResponse<>(
			page.getContent(),
			page.getNumberOfElements(),
			page.hasNext()
		);
	}
}
