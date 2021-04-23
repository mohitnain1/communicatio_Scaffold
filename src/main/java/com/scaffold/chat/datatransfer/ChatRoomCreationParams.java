package com.scaffold.chat.datatransfer;

import java.util.List;

public class ChatRoomCreationParams {
	
	private long creatorId;
	private String chatRoomName;
	private List<Long> membersId;
	
	public long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}
	public String getChatRoomName() {
		return chatRoomName;
	}
	public void setChatRoomName(String chatRoomName) {
		this.chatRoomName = chatRoomName;
	}
	public List<Long> getMembersId() {
		return membersId;
	}
	public void setMembersId(List<Long> membersId) {
		this.membersId = membersId;
	}
	
	
}
