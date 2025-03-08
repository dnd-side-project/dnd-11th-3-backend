package com.dnd.gongmuin.s3.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;

import com.dnd.gongmuin.s3.dto.ImagesUploadResponse;
import com.dnd.gongmuin.s3.dto.VideoUploadRequest;
import com.dnd.gongmuin.s3.dto.VideoUploadResponse;
import com.dnd.gongmuin.s3.service.S3Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "S3 API")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/files")
public class S3Controller {

	private final S3Service s3Service;

	@Operation(summary = "이미지 등록 API", description = "1~10장의 이미지를 등록한다.")
	@ApiResponse(responseCode = "200", description = "Images uploaded successfully")
	@PostMapping("/images")
	public ResponseEntity<ImagesUploadResponse> uploadImages(
			@RequestParam("imageFiles") @Size(min = 1, max = 10) List<MultipartFile> imageFiles) {

		List<String> imageUrls = s3Service.uploadImages(imageFiles);
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
