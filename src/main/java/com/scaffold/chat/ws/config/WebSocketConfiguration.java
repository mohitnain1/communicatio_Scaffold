package com.scaffold.chat.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.chat.ws.event.WebSocketChannelInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	
	@Autowired UsersDetailRepository usersDetailRepository;
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
		registry.addEndpoint("/chat/sockjs").setAllowedOriginPatterns("*").withSockJS();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/queue");
		registry.setApplicationDestinationPrefixes("/app");
	}	

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new WebSocketChannelInterceptor(usersDetailRepository));
	}
}