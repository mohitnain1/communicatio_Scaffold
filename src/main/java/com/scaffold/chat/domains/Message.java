package com.scaffold.chat.domains;

import java.time.LocalDateTime;

public class Message {
	
	private String id;
	private long senderId;
	private String content;
	private LocalDateTime sendingTime;
	private String contentType;
	private String destination;
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
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
	public LocalDateTime getSendingTime() {
		return sendingTime;
	}
	public void setSendingTime(LocalDateTime sendingTime) {
		this.sendingTime = sendingTime;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", senderId=" + senderId + ", content=" + content + ", sendingTime=" + sendingTime
				+ ", destination=" + destination + "]";
	}
}
