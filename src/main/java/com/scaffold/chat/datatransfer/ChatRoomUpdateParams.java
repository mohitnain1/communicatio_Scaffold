package com.scaffold.chat.datatransfer;

import java.util.List;

public class ChatRoomUpdateParams {
	
	private String chatRoomId;
	private List<Long> membersId;
	
	public String getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(String chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public List<Long> getMembersId() {
		return membersId;
	}
	public void setMembersId(List<Long> membersId) {
		this.membersId = membersId;
	}
	
	
}
