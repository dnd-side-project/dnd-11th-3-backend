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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class S3Controller {

	private final S3Service s3Service;

	@PostMapping("/images")
	public ResponseEntity<ImagesUploadResponse> uploadImages(
		@ModelAttribute @Valid ImagesUploadRequest request
	) {
		List<String> imageUrls = s3Service.uploadImages(request.imageFiles());
		return ResponseEntity.ok(ImagesUploadResponse.from(imageUrls));
	}

	@PostMapping("/videos")
	public ResponseEntity<VideoUploadResponse> uploadVideo(
		@ModelAttribute @Valid VideoUploadRequest request
	) {
		String videoUrl = s3Service.uploadVideo(request.videoFile());
		return ResponseEntity.ok(VideoUploadResponse.from(videoUrl));
	}

}
