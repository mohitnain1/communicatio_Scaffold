package com.scaffold.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.service.ChatRoomService;

@RestController
public class MessageController {
	@Autowired public ChatRoomService chatRoomServices;
	
	// Send message in chatRoom....
	@PutMapping(value = "/sendMessage")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> sendMessage(@RequestParam String chatRoomId, @RequestParam long messageSenderId,
			@RequestParam String messageContent) {
		return new ResponseEntity<>(chatRoomServices.sendMessage(chatRoomId, messageSenderId, messageContent),
				HttpStatus.OK);
	}
	
}
