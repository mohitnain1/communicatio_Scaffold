package com.scaffold.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.service.ChatRoomService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
	@Autowired public ChatRoomService chatRoomServices;
	
	//chatRoom creation....
	@PostMapping(value = "/create-chatRoom")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> createChatroom(@RequestParam String chatRoomName, 
			@RequestParam long chatRoomCreatorId, @RequestParam List<Long> chatRoomMemebersId ) {
		return new ResponseEntity<>(chatRoomServices.createChatRoom(chatRoomName, chatRoomCreatorId, chatRoomMemebersId), HttpStatus.OK);
	}
	
}
