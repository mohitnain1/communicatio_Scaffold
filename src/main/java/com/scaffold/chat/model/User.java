package com.scaffold.chat.model;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "usersDetail")
public class User implements Principal {
	@Id
	private String id;
	private long userId;
	private String username;
	private String userType;
	private String userProfilePicture;
	private LocalDateTime userLastSeen;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserProfilePicture() {
		return userProfilePicture;
	}
	public void setUserProfilePicture(String userProfilePicture) {
		this.userProfilePicture = userProfilePicture;
	}
	public LocalDateTime getUserLastSeen() {
		return userLastSeen;
	}
	public void setUserLastSeen(LocalDateTime userLastSeen) {
		this.userLastSeen = userLastSeen;
	}
	@Override
	public String getName() {
		return username;
	}
		
}
