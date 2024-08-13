package com.dnd.gongmuin.s3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.gongmuin.s3.dto.ImagesUploadRequest;
import com.dnd.gongmuin.s3.dto.ImagesUploadResponse;
import com.dnd.gongmuin.s3.dto.VideoUploadRequest;
import com.dnd.gongmuin.s3.dto.VideoUploadResponse;
import com.dnd.gongmuin.s3.service.S3Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "S3 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class S3Controller {

	private final S3Service s3Service;

	@Operation(summary = "이미지 등록 API", description = "1~10장의 이미지를 등록한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/images")
	public ResponseEntity<ImagesUploadResponse> uploadImages(
		@ModelAttribute @Valid ImagesUploadRequest request
	) {
		List<String> imageUrls = s3Service.uploadImages(request.imageFiles());
		return ResponseEntity.ok(ImagesUploadResponse.from(imageUrls));
	}

	@Operation(summary = "동영상 등록 API", description = "최대 45MB의 동영상을 등록한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping("/videos")
	public ResponseEntity<VideoUploadResponse> uploadVideo(
		@ModelAttribute @Valid VideoUploadRequest request
	) {
		String videoUrl = s3Service.uploadVideo(request.videoFile());
		return ResponseEntity.ok(VideoUploadResponse.from(videoUrl));
	}

}
