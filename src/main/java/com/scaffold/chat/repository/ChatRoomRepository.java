package com.scaffold.chat.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.scaffold.chat.domains.ChatRoom;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
	
	Optional<ChatRoom> findByChatRoomIdAndIsDeleted(String chatRoomId, boolean isDeleted);
	Optional<ChatRoom> findByChatRoomNameAndIsDeleted(String chatRoomName, boolean isDeleted);
}
