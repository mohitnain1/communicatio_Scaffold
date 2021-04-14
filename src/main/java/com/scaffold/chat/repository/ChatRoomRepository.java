package com.scaffold.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.scaffold.chat.model.ChatRoom;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
	
	public ChatRoom findByChatRoomId(String chatRoomId);
	
	//@Query("db.chatRoom.update({'chatRoomId' : ?0 }, { $addToSet : { 'chatRoomMemebersId' : ?1} }) ")
	//public ChatRoom findByChatRoom(String chatRoomId, List<Long> chatRoomMemebersId);
}
