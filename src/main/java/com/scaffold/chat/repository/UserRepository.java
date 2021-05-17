package com.scaffold.chat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.scaffold.chat.domains.User;

public interface UserRepository extends MongoRepository<User, String> {
	
	Optional<User> findByUserId(Long userId);
	
	User findByUserId(long userId);
	
	List<User> findAllByIsDeleted(boolean isDeleted);
	
	User findByEmailAndIsDeleted(String email, boolean isDeleted);
}
