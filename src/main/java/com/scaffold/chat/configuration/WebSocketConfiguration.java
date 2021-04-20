package com.scaffold.chat.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.scaffold.chat.ws.handshake.UserInterceptor;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins("http://127.0.0.1:5500").withSockJS();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/user");
		registry.setApplicationDestinationPrefixes("/chat");
	}	
		
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(sessionChannelInterceptor());
	}

	@Bean
	public UserInterceptor sessionChannelInterceptor() {
	   return new UserInterceptor();
	}
}