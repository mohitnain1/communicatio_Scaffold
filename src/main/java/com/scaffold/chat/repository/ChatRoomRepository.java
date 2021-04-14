package com.scaffold.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.scaffold.chat.model.ChatRoom;
import com.scaffold.chat.model.MessageStore;
@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
	
	public ChatRoom findByChatRoomId(String chatRoomId);
	public MessageStore findByMessageStoreId(String chatRoomId);
}
