package com.scaffold.chat.datatransfer;

import java.util.List;

import com.scaffold.security.domains.UserCredentials;

public class ChatRoomUpdateParams {
	
	private String chatRoomId;
	private List<UserCredentials> members;
	
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public List<UserCredentials> getMembers() {
		return members;
	}
	public void setMembers(List<UserCredentials> members) {
		this.members = members;
	}
}
