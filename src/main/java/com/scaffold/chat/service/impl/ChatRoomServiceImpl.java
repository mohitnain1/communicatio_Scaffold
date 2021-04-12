package com.scaffold.chat.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scaffold.chat.model.ChatRoom;
import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.repo.ChatRoomRepository;
import com.scaffold.chat.repo.MessageStoreRepository;
import com.scaffold.chat.service.ChatRoomService;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;

	@Override
	public String createChatRoom(String chatRoomName, String chatRoomCreatorId, List<String> chatRoomMembersId) {
		ChatRoom chatRoom = new ChatRoom(chatRoomName, chatRoomCreatorId, chatRoomMembersId);
		chatRoom.setChatRoomType("Developer-Testing");
		chatRoom.setChatRoomCreationDate(LocalDateTime.now());
		chatRoom.setChatRoomLastConversationDate(LocalDateTime.now());
		chatRoom.setChatRoomId(createChatRoomId(chatRoomName));
		chatRoom.setMessageStore(generateMessageStore(chatRoom.getChatRoomId()));
		ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
		return savedChatRoom.getChatRoomId();
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
		messageDetail.setMessageDestination("/topic/"+chatRoomId);
		messageDetail.setMessageSenderId("Defaut");
		messageDetail.setMessageSendingTime(LocalDateTime.now());
		messageDetail.setMesssageContent("Hello everyone!!");
		return messageDetail;
	}

	private String createChatRoomId(String chatRoomName) {
		LocalDate date = LocalDate.now();
		String roomName = chatRoomName.replaceAll("\\s", ""); 
		return  date+ "-"+roomName.toLowerCase() + "-" + UUID.randomUUID().toString().substring(0, 8);
	}
	
	public List<Message> getMessages(String chatRoomId) {
		return chatRoomRepository.findByChatRoomId(chatRoomId).getMessageStore().getMessageDetails();
	}
}
