package com.scaffold.chat.model;

public class ChatPayload {

	private long senderId;
	private String content;
	private long sendingTime;
	private String destination;
	private String username;
	
	public long getSenderId() {
		return senderId;
	}
	public void setSenderId(long senderId) {
		this.senderId = senderId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getSendingTime() {
		return sendingTime;
	}
	public void setSendingTime(long sendingTime) {
		this.sendingTime = sendingTime;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@Override
	public String toString() {
		return "ChatPayload [senderId=" + senderId + ", content=" + content + ", sendingTime=" + sendingTime
				+ ", destination=" + destination + ", username=" + username + "]";
	}
	
	
}
