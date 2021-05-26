package com.scaffold.chat.domains;

import java.time.LocalDateTime;

public class Member {

	private Long userId;
	private boolean isCreator;
	private LocalDateTime lastSeen;
	
	public Member(Long userId, boolean isCreator) {
		this.userId = userId;
		this.isCreator = isCreator;
	}
	
	public LocalDateTime getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(LocalDateTime lastSeen) {
		this.lastSeen = lastSeen;
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
	
	@Override
	public String toString() {
		return "Member [userId=" + userId + ", isCreator=" + isCreator + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isCreator ? 1231 : 1237);
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Member other = (Member) obj;
		if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
