package com.scaffold.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.scaffold.chat.domains.MessageStore;

@Repository
public interface MessageStoreRepository extends MongoRepository<MessageStore, String>{
	public MessageStore findByChatRoomId(String chatRoomId);
	

}
