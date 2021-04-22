package com.scaffold.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scaffold.chat.model.Message;

@Service
public interface MessageService {
	
	public Message sendMessage(String chatRoomId, long messageSenderId, String messageContent);
	public List<Message> getAllMessages(String chatRoomId);

}