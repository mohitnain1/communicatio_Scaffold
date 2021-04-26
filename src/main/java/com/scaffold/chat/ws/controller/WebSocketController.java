package com.scaffold.chat.ws.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.model.ChatPayload;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.security.domains.UserCredentials;

@RestController
public class WebSocketController {
	
	@Autowired 
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired ChatRoomService chatRoomService;

	@MessageMapping("/chat.{chatRoomId}")
	public Object chatRoom(@DestinationVariable String chatRoomId, @Payload ChatPayload message, UserCredentials principal) {
		message.setUsername(principal.getUsername());
		message.setSendingTime(System.currentTimeMillis());
		
		HashMap<String, Object> chatMessage = new HashMap<String, Object>();
		chatMessage.put("content", message.getContent());
		chatMessage.put("sender", principal);
		chatMessage.put("sendingTime", message.getSendingTime());
		
		simpMessagingTemplate.convertAndSend("/topic/conversations."+chatRoomId, chatMessage);
		return message;
	}
}
