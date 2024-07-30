package com.dnd.gongmuin.s3.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ImagesUploadRequest(
	@NotNull(message = "이미지 파일은 필수 입력 항목입니다.")
	@Size(min=1, max = 10, message = "이미지는 1장 이상 10장 이하로 선택하세요.")
	List<MultipartFile> imageFiles
){
}
