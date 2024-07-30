package com.dnd.gongmuin.s3.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dnd.gongmuin.common.exception.runtime.ValidationException;
import com.dnd.gongmuin.s3.exception.S3ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class S3Service {

	private static final String IMAGE_FOLDER_NAME = "images";
	private static final String VIDEO_FOLDER_NAME = "videos";
	private static final Set<String> ALLOWED_FILE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "mp4", "avi", "mov");
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public List<String> uploadImages(List<MultipartFile> multipartFileList) {
		List<String> fileUrls = new ArrayList<>();

		for (MultipartFile multipartFile : multipartFileList) {
			fileUrls.add(uploadFile(multipartFile, IMAGE_FOLDER_NAME));
		}
		return fileUrls;
	}

	public String uploadVideo(MultipartFile multipartFile) {
		return uploadFile(multipartFile, VIDEO_FOLDER_NAME);
	}

	private String uploadFile(MultipartFile multipartFile, String folderName) {
		ObjectMetadata metadata = new ObjectMetadata();
		String fileName = createFileName(multipartFile.getOriginalFilename());
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		String bucketName = bucket + "/" + folderName;

		try {
			amazonS3.putObject(
				new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), metadata)
			);

		} catch (IOException exception) {
			throw new ValidationException(S3ErrorCode.FAILED_TO_UPLOAD);
		}
		return amazonS3.getUrl(bucketName, fileName).toString();
	}

	private String createFileName(String fileName) {
		return UUID.randomUUID().toString().concat(getFileExtension(fileName));
	}

	private String getFileExtension(String fileName) {
		if (Objects.isNull(fileName) || fileName.isBlank()) {
			throw new ValidationException(S3ErrorCode.EMPTY_FILE_NAME);
		}
		String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
		if (!ALLOWED_FILE_EXTENSIONS.contains(extension)){
			throw new ValidationException(S3ErrorCode.INVALID_FILE_EXTENSION);
		}
		return extension;
	}

}
