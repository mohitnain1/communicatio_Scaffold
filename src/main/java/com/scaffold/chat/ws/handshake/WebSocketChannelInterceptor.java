package com.scaffold.chat.ws.handshake;

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

public class WebSocketChannelInterceptor implements ChannelInterceptor {
	
	private final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
	
	private static final Logger log = LoggerFactory.getLogger(WebSocketChannelInterceptor.class);
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if(accessor.getCommand().equals(StompCommand.CONNECT)) {
			onConnectionRequest(message, accessor);
		} else if(accessor.getCommand().equals(StompCommand.SEND)) {
			onMessage(message, accessor);
		}
		return message;
	}

	@SuppressWarnings("unchecked")
	private void onConnectionRequest(Message<?> message, StompHeaderAccessor accessor) {
		MultiValueMap<String, String> nativeHeaders = message.getHeaders().get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
		UserCredentials credentials = new UserCredentials(0, nativeHeaders.get("imageLink").toString(), nativeHeaders.get("username").toString());
		accessor.setUser(credentials);
	}

	private void onMessage(Message<?> message, StompHeaderAccessor accessor) {
		ChatPayload messagePayload = (ChatPayload)converter.fromMessage(message, ChatPayload.class);
		messagePayload.setMessageDestination(accessor.getDestination());
		log.info("Got Message {}", messagePayload.toString());
	}
}