package com.scaffold.chat.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.scaffold.chat.repository.UserRepository;
import com.scaffold.chat.ws.event.ConnectDisconnectEventHandler;
import com.scaffold.chat.ws.event.WebSocketAuthenticationFilter;
import com.scaffold.security.jwt.JwtUtil;
import com.scaffold.web.util.ScaffoldProperties;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	
	@Autowired UserRepository usersDetailRepository;
	@Autowired ScaffoldProperties properties;
	@Autowired JwtUtil jwtUtil;
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat/sockjs").setAllowedOriginPatterns("*").withSockJS();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");
		registry.enableStompBrokerRelay("/topic", "/queue").setSystemLogin(properties.getBrokerUsername())
			.setSystemPasscode(properties.getBrokerPassword())
			.setRelayHost(properties.getBrokerHostName())
			.setRelayPort(properties.getBrokerPort());
			.setClientLogin(properties.getBrokerUsername())
            .setClientPasscode(properties.getBrokerPassword());
	}	

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new WebSocketAuthenticationFilter(jwtUtil), 
				new ConnectDisconnectEventHandler(usersDetailRepository));
	}
}