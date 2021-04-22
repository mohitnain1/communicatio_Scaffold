package com.scaffold.chat.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
	public List<Message> getAllMessages(String chatRoomId, String chatRoomAccessKey) {
		byte[] accessKey = chatRoomAccessKey.getBytes();
		byte[] roomAccessKey = chatRoomRepository.findByChatRoomId(chatRoomId).getRoomAccessKey();
		if(Arrays.equals(accessKey, roomAccessKey)) {
			MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
			List<Message> messageDetails = messageStore.getMessageDetails();
			return messageDetails;
		} else {
			return new ArrayList<>();
		}
	}

}
