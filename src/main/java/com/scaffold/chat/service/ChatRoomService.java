package com.scaffold.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scaffold.chat.datatransfer.ChatRoomRemoveParams;
import com.scaffold.chat.datatransfer.ChatRoomResponse;
import com.scaffold.security.domains.UserCredentials;

@Service
public interface ChatRoomService {
	
	public ChatRoomResponse createChatRoom(String chatRoomName, UserCredentials chatRoomCreator, List<UserCredentials> chatRoomMembersId);
	
	public String removeChatRoom(ChatRoomRemoveParams removeChatRoom);
	
	public List<UserCredentials> addMembers(String chatRoomId, List<UserCredentials> members);
		
	public List<ChatRoomResponse> userChatRooms(long userId);

	

	
	
}
