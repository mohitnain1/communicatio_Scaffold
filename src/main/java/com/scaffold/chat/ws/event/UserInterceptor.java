package com.scaffold.chat.ws.event;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MultiValueMap;

import com.scaffold.chat.model.ChatPayload;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.security.domains.UserEvent;
import com.scaffold.security.domains.UserSessionRepo;

public class UserInterceptor implements ChannelInterceptor {
	
	private final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
	
	private static final Logger log = LoggerFactory.getLogger(UserInterceptor.class);

	UserSessionRepo userSessions = new UserSessionRepo();
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor.getCommand().equals(StompCommand.CONNECT)) {
			handleSessionConnected(message, accessor);
		} else if (accessor.getCommand().equals(StompCommand.DISCONNECT)) {
			handleSessionDisconnect(message, accessor);
		}else if (accessor.getCommand().equals(StompCommand.SEND)) {
			onMessage(message, accessor);
		}
		return message;
	}

	@SuppressWarnings("unchecked")
	private void handleSessionConnected(Message<?> message, StompHeaderAccessor accessor) {
		MultiValueMap<String, String> nativeHeaders = message.getHeaders().get(StompHeaderAccessor.NATIVE_HEADERS,
				MultiValueMap.class);
		String sessionId = (String) message.getHeaders().get(StompHeaderAccessor.SESSION_ID_HEADER);
		List<String> userData = nativeHeaders.get("userId");
		List<Long> getUserId = userData.stream().map(Long::parseLong).collect(Collectors.toList());
		UserCredentials credentials = new UserCredentials(getUserId.get(0), nativeHeaders.get("imageLink").toString(), nativeHeaders.get("username").toString());
		accessor.setUser(credentials);
		
		List<String> username = nativeHeaders.get("username");
		UserEvent loginEvent = new UserEvent(getUserId.get(0), username.get(0), sessionId);
		userSessions.add(sessionId, loginEvent);	
		log.info("The user connected {}", loginEvent);
	}
	
	private void handleSessionDisconnect(Message<?> message, StompHeaderAccessor accessor) {
		String sessionId = (String) message.getHeaders().get(StompHeaderAccessor.SESSION_ID_HEADER);
		UserEvent logout = userSessions.getParticipant(sessionId);
		logout.setTime(new Date());
		userSessions.removeParticipant(sessionId);
		log.info("The user disconnected {}", logout);
	}
	
	private void onMessage(Message<?> message, StompHeaderAccessor accessor) {
		ChatPayload messagePayload = (ChatPayload)converter.fromMessage(message, ChatPayload.class);
		messagePayload.setMessageDestination(accessor.getDestination());
		log.info("Got Message {}", messagePayload.toString());
	}
}