package com.scaffold.chat.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface MessageService {
	
	public Map<String, Object> getAllMessages(String chatRoomId, String chatRoomAccessKey);
	
	boolean deleteMessage(String messageId, String chatRoomId);

}
