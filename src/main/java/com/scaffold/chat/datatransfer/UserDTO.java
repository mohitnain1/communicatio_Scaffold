package com.scaffold.chat.datatransfer;

import java.util.List;

import lombok.Data;

public @Data class UserDTO {
	
	private long userId;
	private String username;
	private String image;
	private String email;
	private String password;
	private List<String> roles;
}
