package com.scaffold.chat.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.scaffold.chat.model.MessageStore;

@Repository
public interface MessageStoreRepository extends MongoRepository<MessageStore, String>{

}
