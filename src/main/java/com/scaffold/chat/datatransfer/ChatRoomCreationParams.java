package com.scaffold.chat.datatransfer;

import java.util.List;

public class ChatRoomCreationParams {
	
	private String chatRoomName;
	private List<Long> members;
	
	public String getChatRoomName() {
		return chatRoomName;
	}
	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}
	public List<Long> getMembers() {
		return members;
	}
	public void setMembers(List<Long> members) {
		this.members = members;
	}
	@Override
	public String toString() {
		return "ChatRoomCreationParams [chatRoomName=" + chatRoomName + ", members=" + members + "]";
	}
}
