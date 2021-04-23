package com.scaffold.chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.service.MessageService;

@RestController
@RequestMapping("/chat")
public class MessageController {
	
	@Autowired public ChatRoomService chatRoomServices;
	@Autowired public MessageService messageService;
	@Autowired public MessageStoreRepository messageStoreRepository;
	
	@GetMapping(value = "/messages")
	public ResponseEntity<Object> getAllMessages(@RequestParam String chatRoomId, @RequestParam String accessKey) {
		List<Map<String, Object>> message = messageService.getAllMessages(chatRoomId, accessKey);
		if(!message.isEmpty()) {
			return new ResponseEntity<Object>(message, HttpStatus.OK);
		} else {
			return new ResponseEntity<Object>("Invalid Access Key", HttpStatus.UNAUTHORIZED);
		}
	}
	
}
