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

@Service
public class MessageServiceImpl implements MessageService {
	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired private UsersDetailRepository userDetailsRepo;

	@Override
	public List<Map<String, Object>> getAllMessages(String chatRoomId, String chatRoomAccessKey) {
		String roomAccessKey = chatRoomRepository.findByChatRoomId(chatRoomId).getRoomAccessKey();
		if(roomAccessKey.equals(chatRoomAccessKey)) {
			MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
			List<Message> messageDetails = messageStore.getMessageDetails();
			return messageDetails.stream().map(message -> mapMessageResponse(message)).collect(Collectors.toList());
		} else {
			return new ArrayList<>();
		}
	}
	
	public Map<String, Object> mapMessageResponse(Message message) {
		User user = userDetailsRepo.findByUserId(message.getMessageSenderId());
		Map<String, Object> res = new HashMap<>();
		res.put("content", message.getMesssageContent());
		res.put("senderUsername", user == null ? "" : user.getUsername());
		res.put("avatar", user == null ? "fa fa-user" : user.getUserProfilePicture());
		res.put("sendingTime", Timestamp.valueOf(message.getMessageSendingTime()).getTime());
		return res;
	}

}
