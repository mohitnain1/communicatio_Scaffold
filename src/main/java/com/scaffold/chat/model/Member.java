package com.scaffold.chat.model;

public class Member {
	
	private Long userId;
	private boolean isCreator;
	
	public Member(Long userId, boolean isCreator) {
		this.userId = userId;
		this.isCreator = isCreator;
	}
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public boolean isCreator() {
		return isCreator;
	}
	
	public void setCreator(boolean isCreator) {
		this.isCreator = isCreator;
	}
	
	
}
