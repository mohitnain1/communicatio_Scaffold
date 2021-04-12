package com.scaffold.chat.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scaffold.chat.model.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
	ChatRoom findByChatRoomId(String chatRoomId);
}
