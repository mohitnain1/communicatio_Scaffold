package com.scaffold.chat.ws.event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.scaffold.chat.datatransfer.UserDataTransfer;
import com.scaffold.chat.domains.ChatPayload;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UserRepository;
import com.scaffold.web.util.Destinations;
import com.scaffold.web.util.MessageEnum;

@Component
public class VideoCallEvent {
	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired public UserRepository userDetailsRepository;
	@Autowired public SimpMessagingTemplate simpMessagingTemplate;
	@Autowired MessageEventHandler messageEventHandler;
	
	public void incomingCall(Message<ChatPayload> message) {
		com.scaffold.chat.domains.Message savedMessage = messageEventHandler.saveMessage(getCallMessage(message.getPayload()),
				messageEventHandler.getHeaderAccessor(message));
		UserDataTransfer user = getUserBasicDetails(savedMessage.getSenderId());
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(savedMessage)) {
			response.put("id", savedMessage.getId());
			response.put("user", user);
			response.put("sendingTime", savedMessage.getSendingTime());
			response.put("content",savedMessage.getContent());
			response.put("contentType", savedMessage.getContentType());
			String chatRoomId = savedMessage.getDestination().replace("/app/call.", "");
			if(Objects.nonNull(savedMessage)) {
				String destination = String.format("/topic/conversations.", chatRoomId);
				simpMessagingTemplate.convertAndSend(destination, savedMessage);
			}
		}
		new Thread(() -> {messageEventHandler.newMessageEvent(savedMessage, user);}).start();
	}
	
	private ChatPayload getCallMessage(ChatPayload payload) {
		if(payload.getContentType().equals(MessageEnum.CALL_INITIATED.getValue())) {
			payload.setContentType(payload.getContentType());
			payload.setDestination(payload.getDestination());
			payload.setSenderId(payload.getSenderId());
			payload.setUsername(userDetailsRepository.findByUserId(payload.getSenderId()).getUsername());
			payload.setContent("Call stared by " + payload.getUsername());
			payload.setSendingTime(new Date().getTime());
			return payload;
		}
		return null;
	}

	public void callAccepted(ChatPayload chatPayload) {
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(chatPayload.getSenderId())) {
			response.put("user", getUserBasicDetails(chatPayload.getSenderId()));
			response.put("sendingTime", new Date().getTime());
			response.put("contentType", MessageEnum.CALL_ACCEPTED.getValue());
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), chatPayload.getDestination().replace("/app/call.", ""));
			simpMessagingTemplate.convertAndSend(destination, chatPayload);
		}
	}
	
	public void callRejected(ChatPayload chatPayload) {
		Map<String, Object> response = new HashMap<String, Object>();	
		if(Objects.nonNull(chatPayload.getSenderId())) {
			UserDataTransfer user = getUserBasicDetails(chatPayload.getSenderId());
			response.put("user", user);
			response.put("sendingTime", new Date().getTime());
			response.put("content", "Call Declined by "+user.getUsername());
			response.put("contentType", MessageEnum.CALL_REJECTED.getValue());
			String destination = String.format(Destinations.VIDEO_CALL.getPath(), chatPayload.getDestination().replace("/app/call.", ""));
			simpMessagingTemplate.convertAndSend(destination, chatPayload);
		}
	}
	
	private UserDataTransfer getUserBasicDetails(Long userId) {
		return userDetailsRepository.findByUserId(userId)
				.map(user -> new UserDataTransfer(user.getUserId(), user.getImage(), user.getUsername())).orElse(null);
	}
}
