package com.scaffold.chat.datatransfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomResponse {

	private String chatRoomId;
	private String chatRoomName;
	private String roomAccessKey;
	private int totalMembers;
	private long unreadCount;
	
	public long getUnreadCount() {
		return unreadCount;
	}
	public void setUnreadCount(long unreadCount) {
		this.unreadCount = unreadCount;
	}
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public String getChatRoomName() {
		return chatRoomName;
	}
	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}
	public String getRoomAccessKey() {
		return roomAccessKey;
	}
	public void setRoomAccessKey(String roomAccessKey) {
		this.roomAccessKey = roomAccessKey;
	}
	public int getTotalMembers() {
		return totalMembers;
	}
	public void setTotalMembers(int totalMembers) {
		this.totalMembers = totalMembers;
	}
	
	
}
