package com.scaffold.chat.ws.event;

import java.time.LocalDateTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MultiValueMap;

import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.security.domains.UserEvent;
import com.scaffold.security.domains.UserSessionRepo;

public abstract class WebSocketChannelInterceptor implements ChannelInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(WebSocketChannelInterceptor.class);
	final UserSessionRepo userSessions= UserSessionRepo.getInstance();
	
	private UsersDetailRepository userDetailsRepository;
	
	public WebSocketChannelInterceptor(UsersDetailRepository usersDetailRepository) {
		userDetailsRepository = usersDetailRepository;
	}
	
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
		UserCredentials credentials = new UserCredentials(1, nativeHeaders.get("imageLink").toString(), nativeHeaders.get("username").toString());
		userConnectEventHandler(message, accessor, nativeHeaders, credentials);
		accessor.setUser(credentials);
		saveOrUpdateUserInDatabase(credentials);
	}
	
	private void saveOrUpdateUserInDatabase(UserCredentials credentials) {
		userDetailsRepository.findByUserId(credentials.getUserId()).orElseGet(() -> {
			User user = new User();
			user.setUserId(credentials.getUserId());
			user.setUsername(credentials.getUsername());
			user.setUserProfilePicture(credentials.getImageLink());
			user.setUserLastSeen(LocalDateTime.now());
			log.info("Saved new User");
			return userDetailsRepository.save(user);
		});
	}

	private void userConnectEventHandler(Message<?> message, StompHeaderAccessor accessor,
			MultiValueMap<String, String> nativeHeaders, UserCredentials credentials) {
		String sessionId = (String) message.getHeaders().get(StompHeaderAccessor.SESSION_ID_HEADER);
		UserEvent loginEvent = new UserEvent(credentials.getUserId(), credentials.getUsername(), sessionId);
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
	
	protected abstract void onMessage(Message<?> message, StompHeaderAccessor accessor);
}