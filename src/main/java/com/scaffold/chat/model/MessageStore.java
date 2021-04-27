package com.scaffold.chat.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class MessageStore {
	@Id
	private String id;
	private String chatRoomId;
	@DBRef(lazy = true)
	private List<Message> messageDetails;

	public MessageStore(String chatRoomId, List<Message> messageDetails) {
		this.chatRoomId = chatRoomId;
		this.messageDetails = messageDetails;
	}

	public MessageStore() {
	}

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
	
	public void addMessage(Message message) {
		this.messageDetails.add(message);
	}

	@Override
	public String toString() {
		return "MessageStore [id=" + id + ", chatRoomId=" + chatRoomId + ", messageDetails=" + messageDetails + "]";
	}

}
