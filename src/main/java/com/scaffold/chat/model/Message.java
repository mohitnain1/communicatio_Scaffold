package com.scaffold.chat.model;

import java.time.LocalDateTime;

public class Message {
	
	private long messageSenderId;
	private String messsageContent;
	private LocalDateTime messageSendingTime;
	private String messageDestination;
	
	public Message(long messageSenderId, String messsageContent) {
		this.messageSenderId = messageSenderId;
		this.messsageContent = messsageContent;
	}
	
	public Message() {
	}

	public long getMessageSenderId() {
		return messageSenderId;
	}
	public void setMessageSenderId(long messageSenderId) {
		this.messageSenderId = messageSenderId;
	}
	public String getMesssageContent() {
		return messsageContent;
	}
	public void setMesssageContent(String messsageContent) {
		this.messsageContent = messsageContent;
	}
	public LocalDateTime getMessageSendingTime() {
		return messageSendingTime;
	}
	public void setMessageSendingTime(LocalDateTime messageSendingTime) {
		this.messageSendingTime = messageSendingTime;
	}
	public String getMessageDestination() {
		return messageDestination;
	}
	public void setMessageDestination(String messageDestination) {
		this.messageDestination = messageDestination;
	}	
	
	
}
