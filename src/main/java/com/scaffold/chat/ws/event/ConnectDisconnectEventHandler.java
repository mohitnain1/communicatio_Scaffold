package com.scaffold.chat.ws.event;

import java.time.LocalDateTime;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.scaffold.chat.domains.User;
import com.scaffold.chat.repository.UserRepository;
import com.scaffold.security.domains.UserEvent;
import com.scaffold.security.domains.UserSessionRepo;

public class ConnectDisconnectEventHandler implements ChannelInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(ConnectDisconnectEventHandler.class);
	final UserSessionRepo userSessions= UserSessionRepo.getInstance();
	 
	private UserRepository userDetailsRepository;
	
	public ConnectDisconnectEventHandler(UserRepository usersDetailRepository) {
		this.userDetailsRepository = usersDetailRepository;
	}
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
			if (!Objects.isNull(accessor.getCommand()) && accessor.getCommand().equals(StompCommand.CONNECT)) {
				handleSessionConnected(message, accessor);
			} else
			if (!Objects.isNull(accessor.getCommand()) && accessor.getCommand().equals(StompCommand.DISCONNECT)) {
				handleSessionDisconnect(message, accessor);
			}
		return message;
	}

	private void handleSessionConnected(Message<?> message, StompHeaderAccessor accessor) {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
		User user = userDetailsRepository.findByEmailAndIsDeleted(auth.getPrincipal().toString(), false);
		user.setOnline(true);
		userDetailsRepository.save(user);
		Object sessionId = accessor.getHeader(StompHeaderAccessor.SESSION_ID_HEADER);
		userSessions.add(sessionId.toString(), new UserEvent(user.getUserId(), user.getUsername(), sessionId.toString()));
		if(log.isDebugEnabled()) {
			log.debug("User Connected...");
		}
	}

	private void handleSessionDisconnect(Message<?> message, StompHeaderAccessor accessor) {
		UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
		User user = userDetailsRepository.findByEmailAndIsDeleted(auth.getPrincipal().toString(), false);
		user.setOnline(false);
		user.setLastSeen(LocalDateTime.now());
		userDetailsRepository.save(user);
	}
}
