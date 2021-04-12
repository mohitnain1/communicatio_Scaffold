package com.scaffold.chat.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Message {
	
	private String messageSenderId;
	private String messsageContent;
	private LocalDateTime messageSendingTime;
	private String messageDestination;
	
	public String getMessageSenderId() {
		return messageSenderId;
	}
	public void setMessageSenderId(String messageSenderId) {
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
