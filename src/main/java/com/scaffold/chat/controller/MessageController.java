package com.scaffold.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.model.Message;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.service.MessageService;

@RestController
@RequestMapping("/chat")
public class MessageController {
	@Autowired public ChatRoomService chatRoomServices;
	@Autowired public MessageService messageService;
	@Autowired public MessageStoreRepository messageStoreRepository;
	
	// Send message in chatRoom....
	@PutMapping(value = "/message")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> sendMessage(@RequestParam String chatRoomId, 
			@RequestParam long messageSenderId,	@RequestParam String messageContent) {
		try {
			Message message = messageService.sendMessage(chatRoomId, messageSenderId, messageContent);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something error.", HttpStatus.INTERNAL_SERVER_ERROR);
		}	
	}	
	
	//get AllMessage from chatRoom
	@GetMapping(value = "/message")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> getAllMessages(@RequestParam String chatRoomId) {
		try {
			List<Message> message = messageService.getAllMessages(chatRoomId);
			return new ResponseEntity<>(message, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Something error.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
