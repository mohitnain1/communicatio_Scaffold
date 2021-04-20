package com.scaffold.chat.model;

public class ChatPayload {

	private long messageSenderId;
	private String messsageContent;
	private long messageSendingTime;
	private String destination;
	
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
	public long getMessageSendingTime() {
		return messageSendingTime;
	}
	public void setMessageSendingTime(long messageSendingTime) {
		this.messageSendingTime = messageSendingTime;
	}
	public String getMessageDestination() {
		return destination;
	}
	public void setMessageDestination(String destination) {
		this.destination = destination;
	}
	@Override
	public String toString() {
		return "ChatPayload [messageSenderId=" + messageSenderId + ", messsageContent=" + messsageContent
				+ ", messageSendingTime=" + messageSendingTime + ", messageDestination=" + destination + "]";
	}
}
