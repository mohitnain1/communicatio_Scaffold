package com.scaffold.chat.ws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
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
		simpMessagingTemplate.convertAndSend("/topic/conversations."+chatRoomId, message);
		return message;
	}
	
	@SubscribeMapping("/chat.user-chat-rooms")
	public List<String> getUserChatRooms(UserCredentials credentials) {
		return chatRoomService.getUserChatRooms(credentials.getUserId());
	}
}
