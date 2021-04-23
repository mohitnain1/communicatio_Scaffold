package com.scaffold.chat.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface MessageService {
	
	public List<Map<String, Object>> getAllMessages(String chatRoomId, String chatRoomAccessKey);

}
