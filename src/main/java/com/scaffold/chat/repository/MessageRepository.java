package com.scaffold.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.scaffold.chat.model.Message;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

}
