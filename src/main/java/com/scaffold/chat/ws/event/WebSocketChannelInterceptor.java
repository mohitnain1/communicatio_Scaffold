package com.scaffold.chat.ws.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		this.userDetailsRepository = usersDetailRepository;
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
		List<String> userData = nativeHeaders.get("userId");
		List<Long> getUserId = userData.stream().map(Long::parseLong).collect(Collectors.toList());
		UserCredentials credentials = new UserCredentials(getUserId.get(0), nativeHeaders.get("imageLink").get(0), nativeHeaders.get("username").get(0));
		userConnectEventHandler(message, accessor, nativeHeaders, credentials);
		accessor.setUser(credentials);
		saveOrUpdateUserInDatabase(credentials);
	}
	
	private void saveOrUpdateUserInDatabase(UserCredentials credentials) {
		Optional<User> user = userDetailsRepository.findByUserId(credentials.getUserId());
		if(user.isPresent()) {
			user.get().setUserProfilePicture(credentials.getImageLink());
			user.get().setUsername(credentials.getUsername());
			userDetailsRepository.save(user.get());
		}
		else {
			User newUser = new User();
			newUser.setUserId(credentials.getUserId());
			newUser.setUsername(credentials.getUsername());
			newUser.setUserProfilePicture(credentials.getImageLink());
			newUser.setUserLastSeen(LocalDateTime.now());
			log.info("Saved new User");
			userDetailsRepository.save(newUser);
		}
	}

	private void userConnectEventHandler(Message<?> message, StompHeaderAccessor accessor,
			MultiValueMap<String, String> nativeHeaders, UserCredentials credentials) {
		String sessionId = (String) message.getHeaders().get(StompHeaderAccessor.SESSION_ID_HEADER);
		UserEvent login = new UserEvent(credentials.getUserId(), credentials.getUsername(), sessionId);
		userSessions.add(sessionId, login);
		log.info("The user connected {}", login);
	}
	
	private void handleSessionDisconnect(Message<?> message, StompHeaderAccessor accessor) {
		String sessionId = (String) message.getHeaders().get(StompHeaderAccessor.SESSION_ID_HEADER);
		UserEvent logout = userSessions.getParticipant(sessionId);
		logout.setTime(System.currentTimeMillis());
		userSessions.removeParticipant(sessionId);
		log.info("The user disconnected {}", logout);
	}
	
	protected abstract void onMessage(Message<?> message, StompHeaderAccessor accessor);
}
