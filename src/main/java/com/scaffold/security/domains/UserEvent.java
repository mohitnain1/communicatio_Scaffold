package com.scaffold.security.domains;

import java.security.Principal;

public class UserEvent implements Principal {
	private long userId;
	private String username;
	private String sessionId;
	private long time;
	
	public UserEvent(long userId, String username, String sessionId) {
		this.userId = userId;
		this.username = username;
		this.sessionId= sessionId;
		time = System.currentTimeMillis();
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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String getName() {
		return this.username;
	}

	@Override
	public String toString() {
		return "UserDetails [userId=" + userId + ", username=" + username + ", sessionId=" + sessionId + ", time=" + time
				+ "]";
	}
	
	
}
