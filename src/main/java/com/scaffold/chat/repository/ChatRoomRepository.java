package com.scaffold.chat.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.scaffold.chat.model.ChatRoom;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
	
	Optional<ChatRoom> findByChatRoomId(String chatRoomId);
	Optional<ChatRoom> findByChatRoomName(String chatRoomName);
}
