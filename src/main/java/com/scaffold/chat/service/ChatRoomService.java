package com.scaffold.chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scaffold.chat.model.Message;

@Service
public interface ChatRoomService {
	
	public String createChatRoom(String chatRoomName, long chatRoomCreatorId, List<Long> chatRoomMembersId);
	public List<Long> addMembers(String chatRoomId, List<Long> chatRoomMemebersId);
	public List<Long> removeMembers(String chatRoomId, List<Long> chatRoomMemebersId);
	
	public Message sendMessage(String chatRoomId, long messageSenderId, String messageContent);
	
}
