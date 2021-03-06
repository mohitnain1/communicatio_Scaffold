package com.scaffold.chat.datatransfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDataTransfer{

	private long userId;
	private String imageLink;
	private String username;
	private String email;
	private Boolean isCreator = false;

	public UserDataTransfer(long userId, String imageLink, String username) {
		this.userId = userId;
		this.imageLink = imageLink;
		this.username = username;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getImageLink() {
		return imageLink;
	}

	public void setImageLink(String imageLink) {
		this.imageLink = imageLink;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public Boolean getIsCreator() {
		return isCreator;
	}

	public void setIsCreator(Boolean isCreator) {
		this.isCreator = isCreator;
	}
	
	@Override
	public boolean equals(Object obj) {
		UserDataTransfer cred = (UserDataTransfer) obj;
		return this.userId == cred.getUserId();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserCredentials [userId=" + userId + ", imageLink=" + imageLink + ", username=" + username
				+ ", isCreator=" + isCreator + "]";
	}
}
