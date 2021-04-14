package com.scaffold.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.scaffold.chat.service.AWSS3Service;
import com.scaffold.chat.service.ChatRoomService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired public ChatRoomService chatRoomServices;
	@Autowired private AWSS3Service awsService;
	
	//chatRoom creation....
	@PostMapping(value = "/create-chatRoom")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> createChatroom(@RequestParam String chatRoomName, 
			@RequestParam String chatRoomCreatorId, @RequestParam List<String> chatRoomMemebersId ) {
		return new ResponseEntity<>(chatRoomServices.createChatRoom(chatRoomName, chatRoomCreatorId, chatRoomMemebersId), HttpStatus.OK);
	}
	
	//fileUpload on S3 bucket......
	@PostMapping(value= "/uploadFile")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> uploadFile(@RequestPart(value= "file") final MultipartFile multipartFile) {
		String fileName = awsService.uploadFile(multipartFile);
		return new ResponseEntity<>(fileName, HttpStatus.OK);
	}
	
	//download file from s3 bucket.....
	@GetMapping(value= "/downloadFile/{fileName}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> downloadFile(@PathVariable String fileName) {
		byte[] data = awsService.downloadFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(data);
		return ResponseEntity
				.ok()
				.contentLength(data.length)
				.header("content-type", "appilication/octet-stream")
				.header("content-disposition", "attachment; filename=\"" +fileName+ "\"")
				.body(resource);		
	}
	
	//delete file from S3 bucket....
	//@DeleteMapping(value= "/deleteFile/{fileName}")
	@GetMapping(value= "/deleteFile/{fileName}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> deleteFile(@PathVariable String fileName) {
		return new ResponseEntity<>(awsService.deleteFile(fileName), HttpStatus.OK);
	}
}
