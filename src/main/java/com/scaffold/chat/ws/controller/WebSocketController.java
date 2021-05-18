package com.scaffold.chat.ws.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.domains.ChatPayload;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.ws.event.MessageEventHandler;

@RestController
public class WebSocketController {
	
	@Autowired SimpMessagingTemplate simpMessagingTemplate;
	@Autowired ChatRoomService chatRoomService;
	@Autowired MessageEventHandler messageEventHandler;
	
	@MessageMapping("/chat.{chatRoomId}")
	public void chatRoom(@DestinationVariable String chatRoomId, Message<ChatPayload> message) {
		com.scaffold.chat.domains.Message savedMessage = messageEventHandler
				.saveMessage(message.getPayload(), messageEventHandler.getHeaderAccessor(message));
		messageEventHandler.newMessageEvent(savedMessage, messageEventHandler.getCredentials(message));
		if(Objects.nonNull(savedMessage)) {
			simpMessagingTemplate.convertAndSend("/topic/conversations."+chatRoomId, messageEventHandler
					.getResponseForClient(messageEventHandler.getCredentials(message), savedMessage));
		}
	}
}
