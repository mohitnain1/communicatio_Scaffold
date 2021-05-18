package com.scaffold.chat.service;

import java.util.List;

import com.scaffold.chat.datatransfer.UserDTO;
import com.scaffold.chat.domains.User;

public interface UserService {
	
	User saveUser(UserDTO user);
	
	User updateUser(String id,UserDTO user);
	
	boolean deleteUser(String id);
	
	User getUser(String id);
	
	List<User> getAllUsers();
}
