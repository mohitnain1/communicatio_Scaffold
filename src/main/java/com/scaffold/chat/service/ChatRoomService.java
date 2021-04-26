package com.scaffold.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scaffold.chat.datatransfer.ChatRoomResponse;

@Service
public interface ChatRoomService {
	
	public ChatRoomResponse createChatRoom(String chatRoomName, long chatRoomCreatorId, List<Long> chatRoomMembersId);
	
	public List<Long> addMembers(String chatRoomId, List<Long> chatRoomMemebersId);
		
	public List<ChatRoomResponse> userChatRooms(long userId);
	
}
