package com.scaffold.chat.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffold.chat.datatransfer.UserDTO;
import com.scaffold.chat.domains.User;
import com.scaffold.chat.repository.UserRepository;
import com.scaffold.chat.service.UserService;
import com.scaffold.web.util.ScaffoldProperties;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	@Autowired MongoTemplate mongoTemplate;
	@Autowired ObjectMapper objectMapper;
	@Autowired UserRepository userRepository;
	@Autowired ScaffoldProperties properties;
	
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
	
	private static final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	
	@Override
	public User saveUser(UserDTO userPayload) {
		try {
			User user = objectMapper.convertValue(userPayload, User.class);
			user.setPassword(encodeDetails(userPayload.getPassword()));
			return mongoTemplate.save(user);
		}catch(DuplicateKeyException e ) {
			log.info("Unable to save user {}", e.getLocalizedMessage());
			return null;
		}
	}


	@Override
	public User updateUser(String id, UserDTO user) {
		try {
			User existingUser = mongoTemplate.findById(id, User.class);
			if(Objects.nonNull(existingUser)) {
				compareVersionsUpdateDetails(user, existingUser);
				return mongoTemplate.save(existingUser);
			} else {
				return null;
			}
		}catch(DuplicateKeyException e) {
			log.info("Unable to update user {}", e.getLocalizedMessage());
			return null;
		}
	}

	private void compareVersionsUpdateDetails(UserDTO user, User existingUser) {
		existingUser.setImage((user.getEmail() == null || user.getEmail().equals("")) ? 
				existingUser.getImage() : user.getImage());
		existingUser.setUserId(user.getUserId() == 0 ? existingUser.getUserId() : user.getUserId());
		existingUser.setUsername((user.getUsername() == null || user.getUsername().equals("")) ? 
				existingUser.getUsername() : user.getUsername());
	}


	@Override
	public boolean deleteUser(String id) {
		User user = mongoTemplate.findById(id, User.class);
		if(Objects.nonNull(user)) {
			user.setDeleted(true);
			mongoTemplate.save(user);
			return true;
		}
		return false;
	}

	@Override
	public User getUser(String id) {
		User user = mongoTemplate.findById(id, User.class);
		if(Objects.nonNull(user)) {
			return user;
		}
		return null;
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAllByIsDeleted(false);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailAndIsDeleted(username, false);
		if(Objects.isNull(user)) throw new UsernameNotFoundException("Username not found");
		Set<GrantedAuthority> userRoles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role))
				.collect(Collectors.toSet());
		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), userRoles);
	}

	private String encodeDetails(String data) {
		return encoder.encode(data);
	}
	
	public User loadUserByEmail(String email) {
		return userRepository.findByEmailAndIsDeleted(email, false);
	}
}
