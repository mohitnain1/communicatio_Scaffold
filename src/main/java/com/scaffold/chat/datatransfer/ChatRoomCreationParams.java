package com.scaffold.chat.datatransfer;

import java.util.List;

import com.scaffold.security.domains.UserCredentials;

public class ChatRoomCreationParams {
	
	private UserCredentials creator;
	private String chatRoomName;
	private List<UserCredentials> members;
	
	public UserCredentials getCreator() {
		return creator;
	}
	public void setCreator(UserCredentials creator) {
		this.creator = creator;
	}
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
}
