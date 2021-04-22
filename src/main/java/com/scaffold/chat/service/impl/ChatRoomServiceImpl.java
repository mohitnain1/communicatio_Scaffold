package com.scaffold.chat.service.impl;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.scaffold.chat.model.ChatRoom;
import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.chat.service.ChatRoomService;
import com.scaffold.chat.web.UrlConstants;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ChatRoomServiceImpl.class);

	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired UsersDetailRepository userDetailsRepository;
	@Autowired SimpMessagingTemplate simpMessagingTemplate;

	@Override
	public HashMap<String, Object> createChatRoom(String chatRoomName, long chatRoomCreatorId, List<Long> chatRoomMembersId) {
		ChatRoom chatRoom = mapChatRoomCreationDetails(chatRoomName, chatRoomCreatorId, chatRoomMembersId);
		HashMap<String, Object> response = new HashMap<>();
		response.put("chatRoomId", chatRoom.getChatRoomId());
		response.put("accessKey", chatRoom.getRoomAccessKey());
		sendInviteToUsers(chatRoom.getRoomAccessKey(), chatRoom.getChatRoomId(), chatRoomMembersId);
		return response;
	}

	private void sendInviteToUsers(byte[] roomAccessKey, String chatRoomId, List<Long> chatRoomMembersId) {
		HashMap<String, Object> response = new HashMap<>();
		response.put("chatRoomId", chatRoomId);
		response.put("accessKey", roomAccessKey);
		System.out.println(chatRoomMembersId.toString());
		chatRoomMembersId.forEach(member -> {
			System.out.println("/topic/"+member.toString()+"/invitations" + "-----------");
			simpMessagingTemplate.convertAndSend("/topic/"+member.toString()+"/invitations" ,response);
		});
	}

	private ChatRoom mapChatRoomCreationDetails(String chatRoomName, long chatRoomCreatorId,
			List<Long> chatRoomMembersId) {
		ChatRoom chatRoom = new ChatRoom(chatRoomName, chatRoomCreatorId, chatRoomMembersId);
		chatRoom.setChatRoomType("Developer-Testing");
		chatRoom.setChatRoomCreationDate(LocalDateTime.now());
		chatRoom.setChatRoomLastConversationDate(LocalDateTime.now());
		chatRoom.setChatRoomId(createChatRoomId());
		chatRoom.setMessageStore(generateMessageStore(chatRoom.getChatRoomId()));
		try {
			chatRoom.setRoomAccessKey(generateChatRoomAccessKey());
		} catch (NoSuchAlgorithmException e) {
			chatRoom.setRoomAccessKey(UUID.randomUUID().toString().substring(0, 12).getBytes());
		}
		chatRoom = chatRoomRepository.save(chatRoom);
		chatRoomMembersId.add(chatRoomCreatorId);
		addSubscribedChatRoomToUser(chatRoomMembersId, chatRoom.getChatRoomId());
		return chatRoom;
	}

	private void addSubscribedChatRoomToUser(List<Long> chatRoomMembersId, String chatRoomId) {
		chatRoomMembersId.stream().forEach(userId -> {
			userDetailsRepository.findByUserId(userId).ifPresent(user -> {
				List<String> chatRooms = user.getChatRoomIds();
				chatRooms.add(chatRoomId);
				user.setChatRoomIds(chatRooms);
				userDetailsRepository.save(user);
			});
		});
	}
	
	private void removeChatRoomFromUser(List<Long> chatRoomMembersId, String chatRoomId) {
		chatRoomMembersId.stream().forEach(userId -> {
			userDetailsRepository.findByUserId(userId).ifPresent(user -> {
				List<String> chatRooms = user.getChatRoomIds();
				chatRooms.removeIf(room -> room.equalsIgnoreCase(chatRoomId));
				user.setChatRoomIds(chatRooms);
				userDetailsRepository.save(user);
			});
		});
	}

	private String createChatRoomId() {
		String roomName = UUID.randomUUID().toString().substring(0, 6)+System.
				currentTimeMillis()+UUID.randomUUID().toString().substring(0, 6);
		roomName=roomName.replaceAll("[-+.^:,]","");
		return roomName;
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
		addSubscribedChatRoomToUser(savedMemebersId, chatRoomId);
		sendInviteToUsers(allMembersId.getRoomAccessKey(), chatRoomId, newMemebersId);
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
		removeChatRoomFromUser(savedMemebersId, chatRoomId);
		LOGGER.info("Members removed successfully....");
		return allMembersId.getChatRoomMembersId();
	}

	@Override
	public List<String> getUserChatRooms(long userId) {
		return userDetailsRepository.findByUserId(userId).map(User::getChatRoomIds).orElseGet(ArrayList::new);
	}
	
	protected byte[] generateChatRoomAccessKey() throws NoSuchAlgorithmException {
		javax.crypto.KeyGenerator generator = javax.crypto.KeyGenerator.getInstance("HMACSHA1");
		generator.init(120);
		return generator.generateKey().getEncoded();
	}
}
