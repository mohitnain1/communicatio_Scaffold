package com.scaffold.chat.ws.event;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MultiValueMap;

import com.scaffold.chat.model.ChatPayload;
import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.security.domains.UserEvent;
import com.scaffold.security.domains.UserSessionRepo;
import com.scaffold.web.util.Destinations;
import com.scaffold.web.util.SimpleIdGenerator;

public class WebSocketChannelInterceptor implements ChannelInterceptor {
	
	private static final Logger log = LoggerFactory.getLogger(WebSocketChannelInterceptor.class);
	final UserSessionRepo userSessions= UserSessionRepo.getInstance();
	 
	private UsersDetailRepository userDetailsRepository;
	public ChatRoomRepository chatRoomRepository;
	
	@Autowired public SimpMessagingTemplate template;

	private final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

	private final SimpleIdGenerator idGenerator = new SimpleIdGenerator();
	public WebSocketChannelInterceptor(UsersDetailRepository usersDetailRepository, ChatRoomRepository chatRoomRepository) {
		this.userDetailsRepository = usersDetailRepository;
		this.chatRoomRepository= chatRoomRepository;
	}
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor.getCommand().equals(StompCommand.CONNECT)) {
			handleSessionConnected(message, accessor);
		} else if (accessor.getCommand().equals(StompCommand.DISCONNECT)) {
			handleSessionDisconnect(message, accessor);
		}
		return message;
	}
	
	@Override
	public void postSend(Message<?> message, MessageChannel channel, boolean sent ) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (accessor.getCommand().equals(StompCommand.SEND)) {
			newMessageEvent(message, accessor);
		}
	}

	@Override
	public Message<?> postReceive(Message<?> message, MessageChannel channel) {
		return ChannelInterceptor.super.postReceive(message, channel);
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
		User user = userDetailsRepository.findByUserId(credentials.getUserId());
		if(Objects.nonNull(user)) {
			if (!credentials.getImageLink().equals("")) {
			    user.setUserProfilePicture(credentials.getImageLink());
			}
			if (!credentials.getUsername().equals("")) {
			   user.setUsername(credentials.getUsername());
			}
			userDetailsRepository.save(user);
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
	
	private void newMessageEvent(Message<?> message, StompHeaderAccessor accessor) {
		ChatPayload messagePayload = (ChatPayload)converter.fromMessage(message, ChatPayload.class);
		Map<String, Object> senderData = new HashMap<String, Object>();
		senderData.put("senderId", messagePayload.getSenderId());
		senderData.put("senderName", accessor.getUser().getName());
		senderData.put("content", messagePayload.getContent());
		senderData.put("sendingTime", new Date().getTime());
		System.out.println(senderData);
		if(accessor.getDestination().startsWith("/app/chat")) {
			String chatRoomId = accessor.getDestination().replace("/app/chat.", "");
			chatRoomRepository.findByChatRoomId(chatRoomId).ifPresent(chatRoom ->{				
				//List<Long> chatRoomMembersId = chatRoom.getChatRoomMembersId();
//				chatRoomMembersId.forEach(userId->{
//					String destinationToNotify = String.format(Destinations.MESSGE_EVENT_NOTIFICATION.getPath(), userId);
//					template.convertAndSend(destinationToNotify, senderData);
//				});
			});
		}
	}
	
}
