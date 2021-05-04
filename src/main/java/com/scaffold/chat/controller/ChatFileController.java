package com.scaffold.chat.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.datatransfer.FileUploadParms;
import com.scaffold.chat.service.ChatFileUploadService;
import com.scaffold.chat.web.MessageConstants;
import com.scaffold.chat.web.UrlConstants;
import com.scaffold.web.util.Response;

@RestController
public class ChatFileController {

	@Autowired
	private ChatFileUploadService chatFileService;

	@PostMapping(UrlConstants.FILE_UPLOAD)
	public ResponseEntity<Object> upload(@RequestBody FileUploadParms fileUpload, HttpServletRequest request) {
		List<Map<String, Object>> data = chatFileService.uploadFile(fileUpload, request);
		if (data.isEmpty()) {
			return Response.generateResponse(HttpStatus.EXPECTATION_FAILED, null, MessageConstants.FILE_UPLOADED_ERROR, false);
		}
		return Response.generateResponse(HttpStatus.OK, data, MessageConstants.FILE_UPLOADED, true);
	}

	@GetMapping(UrlConstants.FILE_DOWNLOAD)
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName) {
		byte[] data = chatFileService.downloadFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(data);
		fileName = fileName.substring(18);
		if (Objects.isNull(data)) {
			return Response.generateResponse(HttpStatus.OK, null, MessageConstants.FILE_DOWNLOADING_ERROR, false);
		}
		return ResponseEntity.ok().contentLength(data.length).header("content-type", "appilication/octet-stream")
				.header("content-disposition", "attachment; filename=\"" + fileName + "\"").body(resource);
	}

	@DeleteMapping(UrlConstants.FILE_DELETE)
	public ResponseEntity<Object> deleteFile(@PathVariable String fileName) {
		return Response.generateResponse(HttpStatus.OK, chatFileService.deleteFile(fileName), MessageConstants.FILE_DELETED, true);
	}

}
