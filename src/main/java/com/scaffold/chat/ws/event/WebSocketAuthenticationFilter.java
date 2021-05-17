package com.scaffold.chat.ws.event;

import java.util.Objects;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

import com.scaffold.security.jwt.JwtUtil;

public class WebSocketAuthenticationFilter implements ChannelInterceptor {
	
	private JwtUtil jwtUtil;

	public WebSocketAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if(!Objects.isNull(accessor.getCommand()) && accessor.getCommand().equals(StompCommand.CONNECT)) {
			String token = accessor.getFirstNativeHeader("Authorization");
			UsernamePasswordAuthenticationToken principal = verifyAuthentication(token);
			if(Objects.nonNull(principal)) {
				accessor.setUser(principal);
			}
			return message;
		} else {
			return message;
		}
	}

	private UsernamePasswordAuthenticationToken verifyAuthentication(String token) {
		if(Objects.nonNull(token) && token.startsWith("Bearer")) {
			String filtered = token.replace("Bearer", "");
			User user = jwtUtil.getUserFromJWT(filtered);
			return new UsernamePasswordAuthenticationToken(user.getUsername(), "", user.getAuthorities());
		}
		return null;
	}

	 
}
