package com.scaffold.chat.domains;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@NoArgsConstructor
public @Data class User {
	
	@Id
	private String id;
	@Indexed(unique = true)
	private Long userId;
	private String username;
	private String image;
	private LocalDateTime lastSeen;
	private boolean isOnline = false;
	@Indexed(unique = true)
	private String email;
	private String password;
	private boolean isDeleted = false;
	private List<String> roles = new ArrayList<>();
	
	public User(long userId, String username, String image, String email, String password, List<String> roles) {
		this.userId = userId;
		this.username = username;
		this.image = image;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}
	
	
}
