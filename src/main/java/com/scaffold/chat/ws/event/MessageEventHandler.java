package com.scaffold.chat.ws.event;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.scaffold.chat.datatransfer.UserDataTransfer;
import com.scaffold.chat.domains.ChatPayload;
import com.scaffold.chat.domains.Member;
import com.scaffold.chat.domains.MessageStore;
import com.scaffold.chat.domains.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UserRepository;
import com.scaffold.web.util.Destinations;
import com.scaffold.web.util.MessageEnum;
import com.scaffold.web.util.SimpleIdGenerator;

@Component
public class MessageEventHandler {
	
	public MessageStoreRepository messageStoreRepository;
	
	private final SimpleIdGenerator idGenerator = new SimpleIdGenerator();
	
	@Autowired ChatRoomRepository chatRoomRepository;
	@Autowired SimpMessagingTemplate template;
	@Autowired UserRepository userRepository;
	@Autowired AmazonS3 amazonS3;
	@Value("${cloud.aws.bucket.name}")
	private String bucketName;
	
	@Autowired
	public MessageEventHandler(MessageStoreRepository messageStoreRepository) {
		this.messageStoreRepository=messageStoreRepository;
	}

	/**
	 * Get called by {@linkplain WebSocketChannelInterceptor} 
	 * whenever any user sends message at any destination. It
	 * parse the message and saves the record in the database.
	 * 
	 * @param message The WebSocket Message model
	 * @param accessor The Stomp Header accessor.
	 * @return 
	 */
	public com.scaffold.chat.domains.Message saveMessage(ChatPayload messagePayload, StompHeaderAccessor accessor) {
		messagePayload.setSendingTime(System.currentTimeMillis());
		messagePayload.setDestination(accessor.getDestination());
		return saveUserMessageInDatabase(messagePayload);
	}
	
	public com.scaffold.chat.domains.Message saveFileMessage(ChatPayload messagePayload) {
		messagePayload.setSendingTime(System.currentTimeMillis());
		return saveUserMessageInDatabase(messagePayload);
	}
	
	/**
	 * This method gets called whenever user sends any message
	 * to any destination by {@link #onMessage(Message, StompHeaderAccessor)}
	 * and it saves parse the payload and create a message and
	 * saves the record in the database.
	 * 
	 * @param messagePayload Casted Message payload from request.
	 * @return 
	 */
	public com.scaffold.chat.domains.Message saveUserMessageInDatabase(ChatPayload messagePayload) {
		if(!messagePayload.getContent().equals("")) {			
			String messageDestination = messagePayload.getDestination();
			String chatRoomId =messageDestination.substring(messageDestination.indexOf(".")+1);
			MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
			com.scaffold.chat.domains.Message savedMessage = getMessageData(messagePayload);
			messageStore.getMessageDetails().add(savedMessage);
			messageStoreRepository.save(messageStore);
			return savedMessage;
		}
		return null;
	}

	/**
	 * <p>Get called on every message request. Parse the message 
	 * in {@linkplain ChatPayload} and this method cast the payload
	 * into a message representation in database.</p>
	 * 
	 * @param messagePayload The payload representation received in request.
	 * @return {@linkplain com.scaffold.chat.domains.Message}.
	 */
	private com.scaffold.chat.domains.Message getMessageData(ChatPayload messagePayload) {
		com.scaffold.chat.domains.Message messageDetail = new com.scaffold.chat.domains.Message();
		messageDetail.setDestination(messagePayload.getDestination());
		messageDetail.setSenderId(messagePayload.getSenderId());
		messageDetail.setContent(messagePayload.getContent());
		messageDetail.setContentType(messagePayload.getContentType() == null ? MessageEnum.TEXT.getValue() : messagePayload.getContentType());
		messageDetail.setSendingTime(new Timestamp(messagePayload.getSendingTime()).toLocalDateTime());
		messageDetail.setId(idGenerator.generateRandomId());
		return messageDetail;
	}
	
	public UserDataTransfer getCredentials(Message<?> message) {
		UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) StompHeaderAccessor.wrap(message).getUser();
		User userData = userRepository.findByEmailAndIsDeleted(user.getPrincipal().toString(), false);
		return new UserDataTransfer(userData.getUserId(), userData.getImage(), userData.getUsername());
	}
	
	public <T> T getPayload(Message<T> message) {
		return message.getPayload();
	}
	
	public Map<String, Object> getResponseForClient(UserDataTransfer sender, com.scaffold.chat.domains.Message body) {
		HashMap<String, Object> chatMessage = new HashMap<String, Object>();
		chatMessage.put("sender", sender);
		chatMessage.put("sendingTime", Timestamp.valueOf(body.getSendingTime()).getTime());
		chatMessage.put("id", body.getId());
		if(body.getContentType().equals(MessageEnum.IMAGE.getValue())) {
			chatMessage.put("contentType", body.getContentType());
			chatMessage.put("content", getPreSignedUrlForImages(body.getContent()));
		} else {
			chatMessage.put("content", body.getContent());
		}
		return chatMessage;
	}
	
	public String getPreSignedUrlForImages(String fileName) {
		LocalDateTime expiration = LocalDateTime.now();
		expiration = expiration.plusDays(5);
		GeneratePresignedUrlRequest preSignedUrl = new GeneratePresignedUrlRequest(bucketName, fileName)
				.withMethod(HttpMethod.GET)
				.withExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()));
		return amazonS3.generatePresignedUrl(preSignedUrl).toExternalForm();
	}
	
	public StompHeaderAccessor getHeaderAccessor(Message<?> message) {
		return StompHeaderAccessor.wrap(message);
	}
	
	public void newMessageEvent(com.scaffold.chat.domains.Message messagePayload, UserDataTransfer sender) {
		new Thread(() -> {
			Map<String, Object> senderData = new HashMap<String, Object>();
			senderData.put("id", messagePayload.getId());
			senderData.put("sender", sender);
			senderData.put("content", messagePayload.getContent());
			senderData.put("sendingTime", new Date().getTime());
			if(messagePayload.getDestination().startsWith("/app/chat")) {
				String chatRoomId = messagePayload.getDestination().replace("/app/chat.", "");
				chatRoomRepository.findByChatRoomIdAndIsDeleted(chatRoomId, false).ifPresent(chatRoom ->{				
					List<Long> chatRoomMembersId = chatRoom.getMembers().stream().map(Member::getUserId)
							.filter(memberId -> !memberId.equals(sender.getUserId())).collect(Collectors.toList());
					chatRoomMembersId.forEach(userId->{
						senderData.put("chatRoomName", chatRoom.getChatRoomName());
						String destinationToNotify = String.format(Destinations.MESSAGE_EVENT_NOTIFICATION.getPath(), userId);
						template.convertAndSend(destinationToNotify, senderData);
					});
				});
			}
		}).start();
	}
}
