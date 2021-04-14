package com.scaffold.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scaffold.chat.model.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
	public ChatRoom findByChatRoomId(String chatRoomId);
}
