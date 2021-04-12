package com.scaffold.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface ChatRoomService {
	
	public String createChatRoom(String chatRoomName, String chatRoomCreatorId, List<String> chatRoomMembersId);
	
}
