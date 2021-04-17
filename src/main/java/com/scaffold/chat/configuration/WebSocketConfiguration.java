package com.scaffold.chat.configuration;

import java.security.Principal;
import java.util.Map;
import java.util.Random;

import org.apache.http.auth.BasicUserPrincipal;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws").setAllowedOrigins("http://127.0.0.1:5500").setHandshakeHandler(new RandomUsernameHandshakeHandler()).withSockJS();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic", "/user");
		registry.setApplicationDestinationPrefixes("/chat");
	}	
}

class RandomUsernameHandshakeHandler extends DefaultHandshakeHandler {
	
	private String[] ADJECTIVES = {"aggressive", "annoyed", "black", "bootiful", "crazy", "elegant", "happy", 
			"little", "old-fashioned", "secret", "sleepy", "toxic"};
	
	private String[] NOUNS = { "agent", "american", "anaconda", "caiman", "crab", "flamingo", "gorilla", "king", 
			"kitten", "penguin", "runner", "warrior"
	};
	
	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
		Random r = new Random();
		String username = this.getRandom(ADJECTIVES) + "." + this.getRandom(NOUNS) + "-" + r.nextInt(200);
		return new BasicUserPrincipal(username);
	}
	
	private String getRandom(String[] array) {
		Random r = new Random();
		int random = r.nextInt(array.length);
		return array[random];
	}
	
}
