package com.scaffold.chat.ws.event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

	@Override
	protected void onMessage(Message<?> message, StompHeaderAccessor accessor) {
		ChatPayload messagePayload = (ChatPayload)converter.fromMessage(message, ChatPayload.class);
		messagePayload.setMessageDestination(accessor.getDestination());
		saveUserMessageInDatabase(messagePayload);
		log.info("Got Message {}", messagePayload.toString());
	}
	
	public void saveUserMessageInDatabase(ChatPayload messagePayload) {
		String messageDestination = messagePayload.getMessageDestination();
		String chatRoomId =messageDestination.substring(messageDestination.indexOf(".")+1);
		Date date = new Date(messagePayload.getMessageSendingTime());
		LocalDateTime messageSendingTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
		List<com.scaffold.chat.model.Message> messageData = messageStore.getMessageDetails();
		
		com.scaffold.chat.model.Message messageDetail = new com.scaffold.chat.model.Message();
		messageDetail.setMessageDestination(messagePayload.getMessageDestination());
		messageDetail.setMessageSenderId(messagePayload.getMessageSenderId());
		messageDetail.setMesssageContent(messagePayload.getMesssageContent());
		messageDetail.setMessageSendingTime(messageSendingTime);

		messageData.add(messageDetail);
		messageStore.setMessageDetails(messageData);
		messageStoreRepository.save(messageStore);	
	}
}
