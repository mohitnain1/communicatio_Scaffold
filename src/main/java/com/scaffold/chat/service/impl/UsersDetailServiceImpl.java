package com.scaffold.chat.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scaffold.chat.model.User;
import com.scaffold.chat.repo.UsersDetailRepository;
import com.scaffold.chat.service.UsersDetailService;

@Service
public class UsersDetailServiceImpl implements UsersDetailService {
	@Autowired
	public UsersDetailRepository usersDetailRepository;

	@Override
	public User userCreation(User userDetailsModel) {
		User user = usersDetailRepository.save(userDetailsModel);
		return user;
	}	
}
