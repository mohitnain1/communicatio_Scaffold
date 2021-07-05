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
import com.scaffold.chat.ws.event.VideoCallEventHandler;
import com.scaffold.web.util.MessageEnum;

@RestController
public class WebSocketController {
	
	@Autowired SimpMessagingTemplate simpMessagingTemplate;
	@Autowired ChatRoomService chatRoomService;
	@Autowired MessageEventHandler messageEventHandler;
	@Autowired VideoCallEventHandler videoCallEvent;
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
		MessageEnum contentType = MessageEnum.valueOf(String.valueOf(message.getPayload().get("contentType")));
		switch (contentType) {
		case CALL_INITIATED : videoCallEvent.initiateCall(message);
			break;
		case CALL_ACCEPTED : videoCallEvent.callAccepted(message);
			break;
		case CALL_REJECTED : videoCallEvent.callRejected(message);
			break;
		case CALL_DISCONNECTED : videoCallEvent.callDisconnected(message);
			break;
		case RETURNING_SIGNAL : videoCallEvent.returnSignal(message);
			break;
		case SENDING_SIGNAL : videoCallEvent.sendSignal(message);
			break;
		case TOGGLE_AUDIO : videoCallEvent.toggleAudio(message);
			break;
		case ADD_USER_IN_CALL : videoCallEvent.addUserInCall(message);
			break;
		case SCREEN_SHARING : videoCallEvent.screenSharing(message);
		break;
		default: log.error("ContentType don't mached.");
			break;
		}
	}
}
