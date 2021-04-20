package com.scaffold.chat.controller;

import static java.lang.String.format;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.model.Message1;


@RestController
public class WebSocketController {
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;

	@MessageMapping("/chat/{chatRoomId}/sendMessage")
	public Object chatRoom(@DestinationVariable String chatRoomId, @Payload Message1 message, Principal principal) {
		simpMessagingTemplate.convertAndSend(format("/topic/%s", chatRoomId), message);
		return message;
	}
	
    @MessageMapping("/chat/{chatRoomId}/addUser")
    public void addUser(@DestinationVariable String chatRoomId, @Payload Message1 chatMessage,
                        SimpMessageHeaderAccessor headerAccessor) {
        String currentRoomId = (String) headerAccessor.getSessionAttributes().put("room_id", chatRoomId);
        if (currentRoomId != null) {
            Message1 leaveMessage = new Message1();
            leaveMessage.setType(Message1.MessageType.LEAVE);
            leaveMessage.setSender(chatMessage.getSender());
            simpMessagingTemplate.convertAndSend(format("/topic/%s", currentRoomId), leaveMessage);
        }
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    	
        simpMessagingTemplate.convertAndSend(format("/topic/%s", chatRoomId), chatMessage);
    }
    
}
