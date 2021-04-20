package com.scaffold.chat.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class MessageStore {
	@Id
	private String id;
	private String chatRoomId;
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

	@Override
	public String toString() {
		return "MessageStore [id=" + id + ", chatRoomId=" + chatRoomId + ", messageDetails=" + messageDetails + "]";
	}

}
