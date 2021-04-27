package com.scaffold.chat.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
public class Message {
	
	@MongoId(targetType = FieldType.STRING)
	private String id;
	private Long senderId;
	private String content;
	private LocalDateTime sendingTime;
	private String destination;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Long getSenderId() {
		return senderId;
	}
	public void setSenderId(Long senderId) {
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
	
	
}
