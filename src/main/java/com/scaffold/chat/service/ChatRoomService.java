package com.scaffold.chat.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.chat.datatransfer.ChatRoomUpdateParams;
import com.scaffold.chat.datatransfer.UserDataTransfer;

public interface ChatRoomService {
	
	ResponseEntity<Object> createChatRoom(String chatRoomName, List<Long> members);
		
	List<UserDataTransfer> addMembers(ChatRoomUpdateParams params);
		
	List<ChatRoomResponse> userChatRooms(long userId);	
		
	boolean deleteChatRoom(String chatRoomId);
	
}
