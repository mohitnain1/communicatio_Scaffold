package com.scaffold.chat.service.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scaffold.chat.model.Member;
import com.scaffold.chat.model.Message;
import com.scaffold.chat.model.MessageStore;
import com.scaffold.chat.model.User;
import com.scaffold.chat.repository.ChatRoomRepository;
import com.scaffold.chat.repository.MessageStoreRepository;
import com.scaffold.chat.repository.UsersDetailRepository;
import com.scaffold.chat.service.MessageService;
import com.scaffold.security.domains.UserCredentials;
import com.scaffold.security.domains.UserSessionRepo;

@Service
public class MessageServiceImpl implements MessageService {
	
	@Autowired public ChatRoomRepository chatRoomRepository;
	@Autowired public MessageStoreRepository messageStoreRepository;
	@Autowired private UsersDetailRepository userDetailsRepo;
	
	private final UserSessionRepo userSessions = UserSessionRepo.getInstance();

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getAllMessages(String chatRoomId, String chatRoomAccessKey) {
		return (Map<String, Object>) chatRoomRepository.findByChatRoomId(chatRoomId).map(chatRoom -> {
			if(chatRoom.getRoomAccessKey().equals(chatRoomAccessKey)) {
				Map<String, Object> response = new HashMap<>();
				MessageStore messageStore = messageStoreRepository.findByChatRoomId(chatRoomId);
				List<Message> messageDetails = messageStore.getMessageDetails();
				List<Map<String, Object>> messageList = messageDetails.stream().map(message -> mapMessageResponse(message)).collect(Collectors.toList());
				response.put("messages", messageList);
				response.put("members", getMembersResponse(chatRoom.getMembers()));
				return response;
			} else {
				return Collections.emptyMap();
			}
		}).orElse(null);
	}
	
	private List<Map<String, Object>> getMembersResponse(List<Member> members) {
		if (!members.isEmpty()) {
			return members.stream().map(mem -> {
				User user = userDetailsRepo.findByUserId(mem.getUserId().longValue());
				HashMap<String, Object> userDetail = new HashMap<>();
				userDetail.put("userId", mem.getUserId());
				userDetail.put("imageLink", user.getUserProfilePicture());
				userDetail.put("username", user.getUsername());
				userDetail.put("isCreator", mem.isCreator());
				userDetail.put("isOnline", getUserOnlineStatus(mem.getUserId()));
				return userDetail;
			}).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	private boolean getUserOnlineStatus(Long userId) {
		long count = userSessions.getActiveSessions().entrySet().stream()
				.filter(entry -> entry.getValue().getUserId() == userId.longValue()).count();
		if(count > 0) return true;
		return false;
	}

	public Map<String, Object> mapMessageResponse(Message message) {
		User user = userDetailsRepo.findByUserId(message.getSenderId().longValue());
		Map<String, Object> res = new HashMap<>();
		res.put("content", message.getContent());
		res.put("sender", new UserCredentials(user.getUserId(), user.getUserProfilePicture(), user.getUsername()));
		res.put("sendingTime", Timestamp.valueOf(message.getSendingTime()).getTime());
		res.put("id", message.getId());
		res.put("contentType", message.getContentType() == null ? "Text" : message.getContentType());
		return res;
	}

}
