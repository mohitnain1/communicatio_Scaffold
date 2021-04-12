package com.scaffold.chat.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document
public class MessageStore {
	@Id
	private String id;
	private String chatRoomId;
	private List<Message> messageDetails;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatroomId) {
		this.chatRoomId = chatroomId;
	}
	public List<Message> getMessageDetails() {
		return messageDetails;
	}
	public void setMessageDetails(List<Message> messageDetails) {
		this.messageDetails = messageDetails;
	}
}
