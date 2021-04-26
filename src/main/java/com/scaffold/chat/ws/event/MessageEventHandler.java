package com.scaffold.chat.ws.event;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import com.scaffold.chat.model.ChatPayload;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UsersDetailRepository;

public class MessageEventHandler extends WebSocketChannelInterceptor {
	
	public MessageStoreRepository messageStoreRepository;
	
	private static final Logger log = LoggerFactory.getLogger(MessageEventHandler.class);
	private final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

	
	public MessageEventHandler(UsersDetailRepository usersDetailRepository, MessageStoreRepository messageStoreRepository) {
		super(usersDetailRepository);
		this.messageStoreRepository=messageStoreRepository;
	}

	/**
	 * Get called by {@linkplain WebSocketChannelInterceptor} 
	 * whenever any user sends message at any destination. It
	 * parse the message and saves the record in the database.
	 * 
	 * @param message The WebSocket Message model
	 * @param accessor The Stomp Header accessor.
	 */
	@Override
	protected void onMessage(Message<?> message, StompHeaderAccessor accessor) {
		ChatPayload messagePayload = (ChatPayload)converter.fromMessage(message, ChatPayload.class);
		messagePayload.setSendingTime(System.currentTimeMillis());
		messagePayload.setDestination(accessor.getDestination());
		saveUserMessageInDatabase(messagePayload);
		log.info("Got Message {}", messagePayload.toString());
	}
	
	/**
	 * This method gets called whenever user sends any message
	 * to any destination by {@link #onMessage(Message, StompHeaderAccessor)}
	 * and it saves parse the payload and create a message and
	 * saves the record in the database.
	 * 
	 * @param messagePayload Casted Message payload from request.
	 */
	public void saveUserMessageInDatabase(ChatPayload messagePayload) {
		if(!messagePayload.getContent().equals("")) {			
			String messageDestination = messagePayload.getDestination();
			String chatRoomId =messageDestination.substring(messageDestination.indexOf(".")+1);
			MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
			messageStore.addMessage(getMessageData(messagePayload));
			messageStoreRepository.save(messageStore);
		}
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
		messageDetail.setMessageDestination(messagePayload.getDestination());
		messageDetail.setMessageSenderId(messagePayload.getSenderId());
		messageDetail.setMesssageContent(messagePayload.getContent());
		messageDetail.setMessageSendingTime(new Timestamp(messagePayload.getSendingTime()).toLocalDateTime());
		return messageDetail;
	}
}
