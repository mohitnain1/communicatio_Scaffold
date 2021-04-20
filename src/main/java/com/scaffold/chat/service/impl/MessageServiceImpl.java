package com.scaffold.chat.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;

	@Override
	public Message sendMessage(String chatRoomId, long messageSenderId, String messageContent) {
		MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
		List<Message> messageDetails1 = messageStore.getMessageDetails();
		Message messageDetail = new Message();
		messageDetail.setMessageDestination("/topic/" + chatRoomId);
		messageDetail.setMessageSenderId(messageSenderId);
		messageDetail.setMessageSendingTime(LocalDateTime.now());
		messageDetail.setMesssageContent(messageContent);
		messageDetails1.add(messageDetail);
		messageStore.setMessageDetails(messageDetails1);
		messageStoreRepository.save(messageStore);
		LOGGER.info("Message send successfully....");
		return messageDetail;
	}

	@Override
	public List<Message> getAllMessages(String chatRoomId) {
		MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
		List<Message> messageDetails = messageStore.getMessageDetails();
		return messageDetails;
	}

}
