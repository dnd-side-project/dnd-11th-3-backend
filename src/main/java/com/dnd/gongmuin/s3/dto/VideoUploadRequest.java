package com.dnd.gongmuin.s3.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record VideoUploadRequest(
	@NotNull(message = "비디오 파일은 필수 입력 항목입니다.")
	MultipartFile videoFile
) {
}
