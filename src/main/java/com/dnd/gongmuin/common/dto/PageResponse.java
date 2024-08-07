package com.dnd.gongmuin.common.dto;

import java.util.List;

public record PageResponse<T>(
	List<T> content,
	long size,
	boolean hasNext
) {
}
