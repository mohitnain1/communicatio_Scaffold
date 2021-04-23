package com.scaffold.chat.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface ChatRoomService {
	
	public HashMap<String, Object> createChatRoom(String chatRoomName, long chatRoomCreatorId, List<Long> chatRoomMembersId);
	
	public List<Long> addMembers(String chatRoomId, List<Long> chatRoomMemebersId);
	
	public List<Long> removeMembers(String chatRoomId, List<Long> chatRoomMemebersId);
	
	public List<String> getUserChatRooms(long userId);
	
	public HashMap<String, Object> getAllChatRoomOfUser(long userId);
}
