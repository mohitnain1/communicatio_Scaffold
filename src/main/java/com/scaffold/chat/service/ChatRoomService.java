package com.scaffold.chat.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.security.domains.UserCredentials;

@Service
public interface ChatRoomService {
	
	public ResponseEntity<Object> createChatRoom(String chatRoomName, List<UserCredentials> chatRoomMembersId);
		
	public List<UserCredentials> addMembers(String chatRoomId, List<UserCredentials> members);
		
	public List<ChatRoomResponse> userChatRooms(long userId);	
	
	List<UserCredentials> removeMembers(String chatRoomId, List<UserCredentials> members);
	
}
