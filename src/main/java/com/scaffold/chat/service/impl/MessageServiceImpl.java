package com.scaffold.chat.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.chat.service.MessageService;
import com.scaffold.security.domains.UserCredentials;

@Service
public class MessageServiceImpl implements MessageService {
	
	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired private UsersDetailRepository userDetailsRepo;

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getAllMessages(String chatRoomId, String chatRoomAccessKey) {
		return (List<Map<String, Object>>) chatRoomRepository.findByChatRoomId(chatRoomId).map(chatRoom -> {
			if(chatRoom.getRoomAccessKey().equals(chatRoomAccessKey)) {
				MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
				List<Message> messageDetails = messageStore.getMessageDetails();
				System.out.println(messageDetails.size());
				return messageDetails.stream().map(message -> mapMessageResponse(message)).collect(Collectors.toList());
			} else {
				return new ArrayList<>();
			}
		}).orElse(null);
	}
	
	public Map<String, Object> mapMessageResponse(Message message) {
		User user = userDetailsRepo.findByUserId(message.getSenderId().longValue());
		Map<String, Object> res = new HashMap<>();
		res.put("content", message.getContent());
		res.put("sender", new UserCredentials(user.getUserId(), user.getUserProfilePicture(), user.getUsername()));
		res.put("sendingTime", Timestamp.valueOf(message.getSendingTime()).getTime());
		res.put("id", message.getId());
		return res;
	}

}
