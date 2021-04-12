package com.scaffold.chat.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scaffold.chat.model.User;

public interface UsersDetailRepository extends MongoRepository<User, String> {

}
