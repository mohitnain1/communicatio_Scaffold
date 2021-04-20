package com.scaffold.chat.ws.handshake;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.MultiValueMap;

public class UserChannelInterceptor implements ChannelInterceptor {

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		//System.out.println("Channel Interceptor");

		MessageHeaders headers = message.getHeaders();
		StompHeaderAccessor.wrap(message);

		@SuppressWarnings("unchecked")
		MultiValueMap<String, String> multiValueMap = headers.get
				(StompHeaderAccessor.NATIVE_HEADERS,MultiValueMap.class);
		
		for (Entry<String, List<String>> head : multiValueMap.entrySet()) {
			System.out.println(head.getKey() + "---" + head.getValue());
			
		}
		return message;
	}
}