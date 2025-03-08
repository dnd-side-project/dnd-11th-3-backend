package com.dnd.gongmuin.s3.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.gongmuin.s3.dto.ImagesUploadResponse;
import com.dnd.gongmuin.s3.dto.VideoUploadResponse;
import com.dnd.gongmuin.s3.service.S3Service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@Tag(name = "S3 API")
@RestController
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	@Operation(
		summary = "이미지 등록 API",
		description = "1~10장의 이미지를 등록한다."
	)
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping(value = "/api/files/images",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ImagesUploadResponse> uploadImages(
		@RequestPart("imageFiles") @Size(min = 1, max = 10) List<MultipartFile> imageFiles) {
		List<String> imageUrls = s3Service.uploadImages(imageFiles);
		return ResponseEntity.ok(ImagesUploadResponse.from(imageUrls));
	}

	@Operation(summary = "동영상 등록 API", description = "최대 45MB의 동영상을 등록한다.")
	@ApiResponse(useReturnTypeSchema = true)
	@PostMapping(value = "/api/files/videos",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VideoUploadResponse> uploadVideo(
		@RequestPart MultipartFile videoFile
	) {
		String videoUrl = s3Service.uploadVideo(videoFile);
		return ResponseEntity.ok(VideoUploadResponse.from(videoUrl));
	}

}
