package com.scaffold.chat.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.service.MessageService;
import com.scaffold.chat.web.UrlConstants;
import com.scaffold.web.util.Response;

@RestController
public class MessageController {

	@Autowired public ChatRoomService chatRoomServices;
	@Autowired public MessageService messageService;

	@GetMapping(UrlConstants.GET_ALL_MESSAGES)
	public ResponseEntity<Object> getAllMessages(@RequestParam String chatRoomId, @RequestParam String accessKey) {
		Map<String, Object> message = messageService.getAllMessages(chatRoomId, accessKey);
		if (Objects.isNull(message)) {
			return Response.generateResponse(HttpStatus.OK, null, "Invalid Chatroom Id", false);
		} else if (message.isEmpty()) {
			return Response.generateResponse(HttpStatus.OK, null, "Invalid Access key", false);
		} else {
			return Response.generateResponse(HttpStatus.OK, message, "Successful", true);
		}
	}

}
