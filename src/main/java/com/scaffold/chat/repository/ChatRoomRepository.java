package com.scaffold.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.scaffold.chat.model.ChatRoom;
@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
	
	public ChatRoom findByChatRoomId(String chatRoomId);
		
}
