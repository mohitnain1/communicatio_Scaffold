package com.scaffold.chat.ws.controller;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.scaffold.chat.domains.ChatPayload;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.ws.event.MessageEventHandler;
import com.scaffold.chat.ws.event.VideoCallEvent;
import com.scaffold.web.util.MessageEnum;

@RestController
public class WebSocketController {
	
	@Autowired SimpMessagingTemplate simpMessagingTemplate;
	@Autowired ChatRoomService chatRoomService;
	@Autowired MessageEventHandler messageEventHandler;
	@Autowired VideoCallEvent videoCallEvent;
	private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
	
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
	
	@MessageMapping("/call.{chatRoomId}")
	public void videoCall(@DestinationVariable String chatRoomId, Message<Map<String, Object>> message) {
		Map<String, Object> payload = message.getPayload();
		if (String.valueOf(payload.get("contentType")).equals(MessageEnum.CALL_INITIATED.getValue())) {
			videoCallEvent.initiateCall(message);
		}else if (String.valueOf(payload.get("contentType")).equals(MessageEnum.CALL_ACCEPTED.getValue())) {
			videoCallEvent.callAccepted(message);
		}else if (String.valueOf(payload.get("contentType")).equals(MessageEnum.CALL_REJECTED.getValue())) {
			videoCallEvent.callRejected(message);
		}else if (String.valueOf(payload.get("contentType")).equals(MessageEnum.CALL_DISCONNECTED.getValue())) {
			videoCallEvent.callDisconnected(message);
		}else if (String.valueOf(payload.get("contentType")).equals(MessageEnum.RETURNING_SIGNAL.getValue())) {
			videoCallEvent.returnSignal(message);
		}else if (String.valueOf(payload.get("contentType")).equals(MessageEnum.SENDING_SIGNAL.getValue())) {
			videoCallEvent.sendSignal(message);
		}else {
			log.error("ContentType don't matched.");
		}
	}
}
