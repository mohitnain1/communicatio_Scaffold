package com.scaffold.chat.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scaffold.chat.model.User;

public interface UsersDetailRepository extends MongoRepository<User, String> {
	Optional<User> findByUserId(long userId);
	public User findById(long userId);
}
