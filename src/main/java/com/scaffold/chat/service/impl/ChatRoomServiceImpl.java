package com.scaffold.chat.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scaffold.chat.model.ChatRoom;
import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.service.ChatRoomService;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoomServiceImpl.class);

	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;

	@Override
	public String createChatRoom(String chatRoomName, long chatRoomCreatorId, List<Long> chatRoomMembersId) {
		ChatRoom savedChatRoom = null;
		try {
			ChatRoom chatRoom = new ChatRoom(chatRoomName, chatRoomCreatorId, chatRoomMembersId);
			chatRoom.setChatRoomType("Developer-Testing");
			chatRoom.setChatRoomCreationDate(LocalDateTime.now());
			chatRoom.setChatRoomLastConversationDate(LocalDateTime.now());
			chatRoom.setChatRoomId(createChatRoomId(chatRoomName));
			chatRoom.setMessageStore(generateMessageStore(chatRoom.getChatRoomId()));
			savedChatRoom = chatRoomRepository.save(chatRoom);
			LOGGER.info("ChatRoom created successfully....");

		} catch (Exception e) {
			LOGGER.info("ChatRoom creation failed....");
		}
		return savedChatRoom.getChatRoomId();
	}

	private String createChatRoomId(String chatRoomName) {
		LocalDate date = LocalDate.now();
		String roomName = chatRoomName.replaceAll("\\s", "");
		return date + "-" + roomName.toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
	}

	private MessageStore generateMessageStore(String chatRoomId) {
		MessageStore messageStore = new MessageStore();
		messageStore.setChatRoomId(chatRoomId);
		messageStore.setMessageDetails(Arrays.asList(generateWelcomeMessage(chatRoomId)));
		MessageStore savedMessageStore = messageStoreRepository.save(messageStore);
		return savedMessageStore;
	}

	protected Message generateWelcomeMessage(String chatRoomId) {
		Message messageDetail = new Message();
		messageDetail.setMessageDestination("/topic/" + chatRoomId);
		messageDetail.setMessageSenderId(1);
		messageDetail.setMessageSendingTime(LocalDateTime.now());
		messageDetail.setMesssageContent("Hello everyone!!");
		return messageDetail;
	}

	public List<Message> getMessages(String chatRoomId) {
		return chatRoomRepository.findByChatRoomId(chatRoomId).getMessageStore().getMessageDetails();
	}

	@Override
	public List<Long> addMembers(String chatRoomId, List<Long> newMemebersId) {
		ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
		List<Long> savedMemebersId = chatRoom.getChatRoomMembersId();
		newMemebersId.removeAll(savedMemebersId);
		savedMemebersId.addAll(newMemebersId);
		chatRoom.setChatRoomMembersId(savedMemebersId);
		ChatRoom allMembersId = chatRoomRepository.save(chatRoom);
		LOGGER.info("Members added successfully....");
		return allMembersId.getChatRoomMembersId();
	}
	
	@Override
	public List<Long> removeMembers(String chatRoomId, List<Long> removeMemebersId) {
		ChatRoom chatRoom = chatRoomRepository.findByChatRoomId(chatRoomId);
		List<Long> savedMemebersId = chatRoom.getChatRoomMembersId();
		savedMemebersId.removeAll(removeMemebersId);
		chatRoom.setChatRoomMembersId(savedMemebersId);
		ChatRoom allMembersId = chatRoomRepository.save(chatRoom);
		LOGGER.info("Members removed successfully....");
		return allMembersId.getChatRoomMembersId();
	}	
	
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
}
