package com.scaffold.chat.ws.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.model.ChatPayload;
import com.scaffold.security.domains.UserCredentials;

@RestController
public class WebSocketController {
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/chat/{chatRoomId}.conversations")
	public Object chatRoom(@DestinationVariable String chatRoomId, @Payload ChatPayload message, UserCredentials principal) {
		simpMessagingTemplate.convertAndSend("/topic/chat."+chatRoomId+".conversations", message);
		return message;
	}    
}
