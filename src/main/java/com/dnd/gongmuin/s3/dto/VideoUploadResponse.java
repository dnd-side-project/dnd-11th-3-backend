package com.dnd.gongmuin.s3.dto;

public record VideoUploadResponse(
	String videoUrl
) {
	public static VideoUploadResponse from(String videoUrl) {
		return new VideoUploadResponse(videoUrl);
	}
}
