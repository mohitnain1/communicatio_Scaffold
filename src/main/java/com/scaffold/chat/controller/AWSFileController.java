package com.scaffold.chat.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scaffold.chat.service.AWSS3Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/chat")
@Api(value = "AWS File Controller")
public class AWSFileController {
	
	@Autowired private AWSS3Service awsService;

	// fileUpload on S3 bucket........
	@ApiOperation(value = "Upload File on S3 Bucket", notes = "This api is used to upload the file on S# Bucket.")
	@PostMapping(value = "/upload")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> uploadFile(@RequestPart(value = "file") 
		final MultipartFile multipartFile,HttpServletRequest request) {
			String fileName = awsService.uploadFile(multipartFile, request);
			return new ResponseEntity<>(fileName, HttpStatus.OK);
	}

	// download file from s3 bucket.....
	@ApiOperation(value = "Download File from S3 Bucket", notes = "This api is used to download the file from S3 Bucket.")
	@GetMapping(value = "/download/{fileName}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName) {
		byte[] data = awsService.downloadFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(data);
		return ResponseEntity.ok()
				.contentLength(data.length)
				.header("content-type", "appilication/octet-stream")
				.header("content-disposition", "attachment; filename=\"" + fileName + "\"")
				.body(resource);
	}

	// delete file from S3 bucket....
	@ApiOperation(value = "Delete File from S3 Bucket", notes = "This api is used to delete the file from S3 Bucket.")
	@DeleteMapping(value = "/delete/{fileName}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> deleteFile(@PathVariable String fileName) {
		return new ResponseEntity<>(awsService.deleteFile(fileName), HttpStatus.OK);
	}
}
