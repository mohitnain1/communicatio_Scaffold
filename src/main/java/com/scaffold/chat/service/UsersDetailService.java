package com.scaffold.chat.service;

import org.springframework.stereotype.Service;

import com.scaffold.chat.model.User;
@Service
public interface UsersDetailService {
	public User userCreation(User userDetailsModel);

}
