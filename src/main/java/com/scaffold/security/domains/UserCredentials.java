package com.scaffold.security.domains;

import java.security.Principal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCredentials implements Principal {

	private long userId;
	private String imageLink;
	private String username;

	public UserCredentials(long userId, String imageLink, String username) {
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

	@Override
	public String getName() {
		return this.username;
	}

	@Override
	public boolean equals(Object obj) {
		UserCredentials cred = (UserCredentials) obj;
		return this.userId == cred.getUserId();
	}

	@Override
	public String toString() {
		return "UserCredentials [userId=" + userId + ", imageLink=" + imageLink + ", username=" + username + "]";
	}
}
