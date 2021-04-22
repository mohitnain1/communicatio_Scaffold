package com.scaffold.chat.ws.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import com.scaffold.chat.model.ChatPayload;
import com.scaffold.chat.repository.UsersDetailRepository;

public class MessageEventHandler extends WebSocketChannelInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(MessageEventHandler.class);
	
	private final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
	
	public MessageEventHandler(UsersDetailRepository usersDetailRepository) {
		super(usersDetailRepository);
	}

	@Override
	protected void onMessage(Message<?> message, StompHeaderAccessor accessor) {
		ChatPayload messagePayload = (ChatPayload)converter.fromMessage(message, ChatPayload.class);
		messagePayload.setMessageDestination(accessor.getDestination());
		log.info("Got Message {}", messagePayload.toString());
	}

}
