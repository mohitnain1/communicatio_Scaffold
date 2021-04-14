package com.scaffold.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface ChatRoomService {
	
	public String createChatRoom(String chatRoomName, long chatRoomCreatorId, List<Long> chatRoomMembersId);
	public List<Long> addMembersInChatRoom(String chatRoomId, List<Long> membersId);
	
}
