package com.scaffold.chat.datatransfer;

import java.util.List;

import com.scaffold.security.domains.UserCredentials;

public class ChatRoomCreationParams {
	
	private String chatRoomName;
	private List<UserCredentials> members;
	
	public String getChatRoomName() {
		return chatRoomName;
	}
	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}
	public List<UserCredentials> getMembers() {
		return members;
	}
	public void setMembers(List<UserCredentials> members) {
		this.members = members;
	}
	@Override
	public String toString() {
		return "ChatRoomCreationParams [chatRoomName=" + chatRoomName + ", members=" + members + "]";
	}
}
