package com.scaffold.chat.ws.event;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import com.scaffold.chat.model.ChatPayload;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.web.util.SimpleIdGenerator;

@Component
public class MessageEventHandler {
	
	public MessageStoreRepository messageStoreRepository;
	
	private static final Logger log = LoggerFactory.getLogger(MessageEventHandler.class);
	private final SimpleIdGenerator idGenerator = new SimpleIdGenerator();
	
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
	public com.scaffold.chat.model.Message saveMessage(ChatPayload messagePayload, StompHeaderAccessor accessor) {
		messagePayload.setSendingTime(System.currentTimeMillis());
		messagePayload.setDestination(accessor.getDestination());
		log.info("Got Message {}", messagePayload.toString());
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
	public com.scaffold.chat.model.Message saveUserMessageInDatabase(ChatPayload messagePayload) {
		if(!messagePayload.getContent().equals("")) {			
			String messageDestination = messagePayload.getDestination();
			String chatRoomId =messageDestination.substring(messageDestination.indexOf(".")+1);
			MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
			com.scaffold.chat.model.Message savedMessage = getMessageData(messagePayload);
			messageStore.addMessage(savedMessage);
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
	 * @return {@linkplain com.scaffold.chat.model.Message}.
	 */
	private com.scaffold.chat.model.Message getMessageData(ChatPayload messagePayload) {
		com.scaffold.chat.model.Message messageDetail = new com.scaffold.chat.model.Message();
		messageDetail.setDestination(messagePayload.getDestination());
		messageDetail.setSenderId(messagePayload.getSenderId());
		messageDetail.setContent(messagePayload.getContent());
		messageDetail.setSendingTime(new Timestamp(messagePayload.getSendingTime()).toLocalDateTime());
		messageDetail.setId(idGenerator.generateRandomId());
		return messageDetail;
	}
	
	public UserCredentials getCredentials(Message<?> message) {
		return (UserCredentials) StompHeaderAccessor.wrap(message).getUser();
	}
	
	public <T> T getPayload(Message<T> message) {
		return message.getPayload();
	}
	
	public Map<String, Object> getResponseForClient(UserCredentials sender, com.scaffold.chat.model.Message body) {
		HashMap<String, Object> chatMessage = new HashMap<String, Object>();
		chatMessage.put("content", body.getContent());
		chatMessage.put("sender", sender);
		chatMessage.put("sendingTime", Timestamp.valueOf(body.getSendingTime()).getTime());
		chatMessage.put("id", body.getId());
		return chatMessage;
	}
	
	public StompHeaderAccessor getHeaderAccessor(Message<?> message) {
		return StompHeaderAccessor.wrap(message);
	}
}
