package com.dnd.gongmuin.s3.dto;

import java.util.List;

public record ImagesUploadResponse(
	List<String> imageUrls
) {
	public static ImagesUploadResponse from(List<String> imageUrls) {
		return new ImagesUploadResponse(imageUrls);
	}
}